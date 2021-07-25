package ribbonconfiguration;

import com.example.demo.configuration.NacosSameClusterWeightedRule;
import com.example.demo.configuration.NacosSameVersionRule;
import com.example.demo.configuration.NacosWeightedRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.WeightedResponseTimeRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
//        return new RandomRule();
//        return new RoundRobinRule();
//        return new WeightedResponseTimeRule();

        // 自己定义的 Nacos Weighted Rule,重载基于Nacos Client的负载均衡算法
//        return new NacosWeightedRule();

        // 自己定义的 相同集群 优先的，负载均衡算法
//        return new NacosSameClusterWeightedRule();

        // 自己定义的 相同版本 优先的，负载均衡算法
        return new NacosSameVersionRule();
    }

}
