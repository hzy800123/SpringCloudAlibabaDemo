package com.example.demo.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 基于 相同集群 优先，再根据 权重的负载均衡算法 进行选择。
 */
@Slf4j
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        try {
            // 拿到配置文件中的 集群名称 (e.g.Guangzhou, Shanghai)
            String clusterName = nacosDiscoveryProperties.getClusterName();

            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            // 想要请求的 微服务的 名称
            String name = loadBalancer.getName();
            log.info("Micro Service Name: {}", name);
            // 拿到 服务发现 的相关API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();

            // 1。找到指定服务的 所有实例
            List<Instance> instances = namingService.selectInstances(name, true);

            // 2。过滤出 相同集群下 的所有实例
            List<Instance> sameClusterInstances = instances.stream()
                    .filter(instance -> Objects.equals(instance.getClusterName(), clusterName))
                    .collect(Collectors.toList());

            // 3。如果 相同集群 里的实例数量 是空，就用 原来的实例。
            // 否则，优先采用 相同集群 里的实例，再根据 负载均衡算法 来选择。
            List<Instance> instancesToBeChosen = new ArrayList<>();
            if(CollectionUtils.isEmpty(sameClusterInstances)) {
                instancesToBeChosen = instances;
                log.warn("发生 跨集群 的调用， name = {}, cluster = {}", name, clusterName);
            } else {
                instancesToBeChosen = sameClusterInstances;
            }

            // 4。基于权重的负载均衡算法，返回一个实例
            Instance instance = ExtendBalancer.getHostByRandomWeight2(instancesToBeChosen);
            log.info("选择的实例是：port = {}, instance = {}", instance.getPort(), instance);

            return new NacosServer(instance);
        } catch (NacosException e) {
            e.printStackTrace();
            log.error("发生异常了", e);
            return null;
        }
    }
}

class ExtendBalancer extends Balancer {
    public static Instance getHostByRandomWeight2(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}