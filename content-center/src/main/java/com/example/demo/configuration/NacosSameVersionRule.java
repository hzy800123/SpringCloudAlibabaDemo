package com.example.demo.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
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

@Slf4j
public class NacosSameVersionRule extends AbstractLoadBalancerRule {
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        try {
            // 拿到配置文件中的 集群名称 (e.g.Guangzhou, Shanghai)
            String version = nacosDiscoveryProperties.getMetadata().get("version");

            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            // 想要请求的 微服务的 名称
            String name = loadBalancer.getName();
            log.info("Micro Service Name: {}", name);
            // 拿到 服务发现 的相关API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();

            // 1。找到指定服务的 所有实例
            List<Instance> instances = namingService.selectInstances(name, true);

            // 2。过滤出 相同版本下 的所有实例
            List<Instance> sameVersionInstances = instances.stream()
                    .filter(instance -> Objects.equals(instance.getMetadata().get("version"), version))
                    .collect(Collectors.toList());

            // 3。如果 相同版本 里的实例数量 是空，就用 原来的实例。
            // 否则，优先采用 相同版本 里的实例，再根据 负载均衡算法 来选择。
            List<Instance> instancesToBeChosen = new ArrayList<>();
            if(CollectionUtils.isEmpty(sameVersionInstances)) {
                instancesToBeChosen = instances;
                log.warn("发生 跨版本 的调用， name = {}, version = {}", name, version);
            } else {
                instancesToBeChosen = sameVersionInstances;
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
