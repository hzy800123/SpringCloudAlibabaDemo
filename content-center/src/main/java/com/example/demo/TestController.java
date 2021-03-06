package com.example.demo;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.demo.domain.entity.user.Response;
import com.example.demo.domain.entity.user.User;
import com.example.demo.feignclient.FeignClientWithoutNacosAndRibbon;
import com.example.demo.feignclient.UserCenterFeignClient;
import com.example.demo.feignclient.UserCenterFeignClientMultiParam;
import com.example.demo.sentinelblock.TestControllerBlockHandler;
import com.example.demo.sentinelblock.TestControllerFallbackHandler;
import io.netty.util.concurrent.CompleteFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    // Spring Cloud 的接口
    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserCenterFeignClient userCenterFeignClient;

    @Autowired
    private UserCenterFeignClientMultiParam userCenterFeignClientMultiParam;

    @Autowired
    private FeignClientWithoutNacosAndRibbon feignClientWithoutNacosAndRibbon;

    @Autowired
    private ServiceTest serviceTest;

//    @GetMapping("/1")
//    public String test() {
//        log.info("test /1");
//        return "test successfully !";
//    }

    @SentinelResource("/test/{id}")
    @GetMapping("/{id}")
    public String testNumber(@PathVariable Integer id) {
        log.info("test/{}", id);
        return "/test/" + id;
    }
    


    /**
     * 测试：服务发现，证明 内容中心 总能找到 用户中心
     * @return 用户中心 所有实例的地址信息
     */
    @GetMapping("/testnacos")
    public List<ServiceInstance> getInstance() {
        // 查询 指定服务的 所有实例的信息
        return this.discoveryClient.getInstances("user-center");
    }

    /**
     * 获取 当前服务发现组件里 组册的全部 微服务名称
     * @return 组册的全部 微服务名称
     */
    @GetMapping("/getservices")
    public List<String> getServices() {
        return this.discoveryClient.getServices();
    }

    /**
     * 使用 RestTemplate, 获取 微服务（用户中心）的目标URL地址
     * @return User
     */
    @GetMapping("/getuserinfo/{id}")
//    @GetMapping("/getuserinfo")
    public User getUserCenterURL(@PathVariable Integer id) throws IllegalArgumentException {
//    public User getUserCenterUL(@RequestParam Integer id) throws IllegalArgumentException{
        // 从 Nacos 服务注册中心，查找 user-center 微服务的实例
        List<ServiceInstance> instances = this.discoveryClient.getInstances("user-center");

        // 获取 用户中心 的所有实例的信息 （例如：URL IP地址信息）
        List<String> targetURLfromNacos = instances.stream()
                .map(instance -> instance.getUri().toString() + "/API/users/{id}")
                .collect(Collectors.toList());

        // 随机 选取一个实例的URL
        int i = ThreadLocalRandom.current().nextInt(targetURLfromNacos.size());
        String targetURL = targetURLfromNacos.get(i);

        log.info("Nacos 目标 微服务 的地址URL: {}", targetURLfromNacos);
        log.info("随机 目标 微服务 的地址URL: {}", targetURL);

        // 调用 用户中心 的API，返回用户记录信息
//        User userInfo = restTemplate.getForObject(
        ResponseEntity<User> userInfo = this.restTemplate.getForEntity(
//                targetURLfromNacos,
                targetURL,
                User.class,
                id
        );

        log.info("Status Code: {}", userInfo.getStatusCode());
        log.info(userInfo.getBody().toString());
//        log.info(userInfo.toString());

        return userInfo.getBody();
//        return userInfo;
    }


    /**
     * 使用 RestTemplate，通过 Ribbon，使用 负载均衡 LoadBalanced，获取 微服务（用户中心）的目标URL地址
     * 整合 Ribbon 需要在入口class 加上注解：@LoadBalanced
     * @return User
     */
    @GetMapping("/getuserinforibbon/{id}")
    public User getUserCenterByRibbon(@PathVariable Integer id) throws IllegalArgumentException {
        String targetURL = "http://user-center/API/users/{id}";

        // 调用 用户中心 的API，返回用户记录信息
//        User userInfo = this.restTemplate.getForObject(
        ResponseEntity<User> userInfo = this.restTemplate.getForEntity(
                targetURL,
                User.class,
                id
        );

        log.info("Status Code: {}", userInfo.getStatusCode());
        log.info(userInfo.getBody().toString());
//        log.info(userInfo.toString());

        return userInfo.getBody();
//        return userInfo;
    }

    /**
     * 通过 Feign，调用 微服务user-center（用户中心）
     * @return User
     */
    @GetMapping("/getuserinfofeign/id-list")
    public List<User> getUserCenterByFeignWithIdList() throws IllegalArgumentException {
        List<Integer> idList = new ArrayList<>();
        idList.add(1);
        idList.add(2);
        idList.add(3);

        // 调用 用户中心 的API，返回用户记录信息
        log.info("通过Feign，调用 微服务user-center");
        List<User> userInfoList = this.userCenterFeignClient.findByIdList(idList);

        log.info(userInfoList.toString());
        return userInfoList;
    }


    @GetMapping("/getuserinfofeign/id-list-concurrently")
    public List<User> getUserCenterByFeignWithIdListConcurrently(@RequestParam Integer totalSize)
            throws IllegalArgumentException, ExecutionException, InterruptedException {
        List<Integer> idListConcurrently = new ArrayList<>();

        int sizeTestUserId = totalSize;
        for(int i = 0; i < sizeTestUserId; i++) {
            int randomNumber = getRandomNumber(1, 10);
            idListConcurrently.add(randomNumber);
        }

        List<User> userInfoList = new ArrayList<>();

        int sizeList = idListConcurrently.size();
        CountDownLatch countDownLatch = new CountDownLatch(sizeList);
        CountDownLatch countDownLatchForResponse = new CountDownLatch(sizeList);

        for (Integer userId : idListConcurrently) {
            Thread thread = new Thread(
                    () -> {
                        try {
                            countDownLatch.countDown();
                            countDownLatch.await();

                            // 调用 用户中心 的API，返回用户记录信息
//                            log.info("After countDownLatch await, start to call API.");
                            User user = getUserCenterByFeign(userId);
                            userInfoList.add(user);
                            countDownLatchForResponse.countDown();
                        } catch (InterruptedException e) {
                            log.error("Catch Exception after call API.");
                            e.printStackTrace();
                        }
                    }
            );
            thread.start();
        }

        long startTime = System.nanoTime();
        log.info("Waiting for All response received.");
        countDownLatchForResponse.await();
        log.info("Return the userInfoList size - {}", userInfoList.size());
        long endTime = System.nanoTime();
        calculateAndDisplayCostTime(startTime, endTime, "Concurrently - Call API to get All User");

        return userInfoList;
    }


    @GetMapping("/getuserinfofeign/id-list-in-batch")
    public List<User> getUserCenterByFeignWithIdListInBatch(@RequestParam Integer totalSize)
            throws IllegalArgumentException, ExecutionException, InterruptedException {
        List<Integer> idListInBatch = new ArrayList<>();

        int sizeTestUserId = totalSize;
        for(int i = 0; i < sizeTestUserId; i++) {
            int randomNumber = getRandomNumber(1, 10);
            idListInBatch.add(randomNumber);
        }

        List<User> userInfoList = new ArrayList<>();

        int sizeList = idListInBatch.size();
        CountDownLatch countDownLatch = new CountDownLatch(sizeList);
        CountDownLatch countDownLatchForResponse = new CountDownLatch(sizeList);

        for (Integer userId : idListInBatch) {
            Thread thread = new Thread(
                    () -> {
                        try {
                            countDownLatch.countDown();
                            countDownLatch.await();

                            // 调用 用户中心 的API，返回用户记录信息
//                            log.info("After countDownLatch await, start to call API.");
                            User user = getUserInBatch(userId);
                            userInfoList.add(user);
                            countDownLatchForResponse.countDown();
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            log.error("Catch Exception after call API.");
                            e.printStackTrace();
                        }
                    }
            );
            thread.start();
        }

        long startTime = System.nanoTime();
        log.info("Waiting for All response received.");
        countDownLatchForResponse.await();
//        Thread.sleep(3000);
        log.info("Return the userInfoList size - {}", userInfoList.size());
        long endTime = System.nanoTime();
        calculateAndDisplayCostTime(startTime, endTime, "In Batch - Call API to get All User");

        return userInfoList;
    }


    private void calculateAndDisplayCostTime(long startTime, long endTime, String description) {
        long usedTime = endTime - startTime;
        long millisecondTime = TimeUnit.MILLISECONDS.convert(usedTime, TimeUnit.NANOSECONDS);
        log.warn(description + " - {} milliseconds", millisecondTime);
    }


    private int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }


    @Data
    class Request {
        String serialNo = "";
        Integer userId = 0;
        CompletableFuture<User> futureUser;
    }

    // 链表的阻塞队列
    LinkedBlockingQueue<Request> blockingQueue = new LinkedBlockingQueue<>();

    // 通过 后端的的批量访问接口，并使用CompletableFuture调用，异步返回结果。
    public User getUserInBatch(Integer userId)
            throws ExecutionException, InterruptedException, TimeoutException {

        // 对于每个Request，生成唯一的 serialNo
        String serialNo = UUID.randomUUID().toString();

        // CompletableFuture 会监听结果（线程），然后自动调用get()返回结果
        CompletableFuture<User> futureUser = new CompletableFuture<>();

        Request request = new Request();
        request.setSerialNo(serialNo);
        request.setUserId(userId);
        request.setFutureUser(futureUser);

        // 把请求，加入阻塞队列
        blockingQueue.add(request);

        // 如果没有结果，会阻塞；如果有结果，会自动返回结果
        User user = futureUser.get(10000, TimeUnit.MILLISECONDS);
//        log.info("After call API, return user: {}", user);
        return user;
    }

    @PostConstruct
    public void doScheduleTask() {

        ScheduledExecutorService scheduledThreadPool = new ScheduledThreadPoolExecutor(1);
        int maxProcessInEachBatch = 1000;

        scheduledThreadPool.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        int queueSize = blockingQueue.size();
                        if (queueSize == 0) {
                            return;
                        }
                        log.info("queueSize = {}", queueSize);

                        List<Request> requestList = new ArrayList<>();
                        List<Map<String, String>> requestAPIBodyList = new ArrayList<>();
                        int actualProcessSize = Math.min(queueSize, maxProcessInEachBatch);

//                        for(int i = 0; i < queueSize; i++) {
                        for(int i = 0; i < actualProcessSize; i++) {
                            Request request = blockingQueue.poll();
                            requestList.add(request);

                            Map<String, String> map = new HashMap<>();
                            map.put("serialNo", request.getSerialNo());
                            map.put("userId", request.getUserId().toString());
                            requestAPIBodyList.add(map);
                        }
                        log.info("批量调用API - size = {}", requestAPIBodyList.size());

                        // 调用 用户中心 的API，返回用户记录信息
                        log.info("通过Feign，调用 微服务user-center");
                        List<Response> userInfoList = userCenterFeignClient.findByIdListInBatch(requestAPIBodyList);

                        int notifyCount = 0;
                        for(Request request : requestList) {
                            String requestSerialNo = request.getSerialNo();

                            for(Response userInfo : userInfoList) {
                                String responseSerialNo = (String) userInfo.getSerialNo();

                                if(requestSerialNo.equals(responseSerialNo)) {
                                    User responseUser = (User) userInfo.getUser();
                                    request.getFutureUser().complete(responseUser);
                                    notifyCount++;
                                    break;
                                }
                            }
                        }
                        log.info("Complete - Notify Count = {}", notifyCount);
                    }
                },
                1000,
                100,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 通过 Feign，调用 微服务user-center（用户中心）
     * @return User
     */
    @GetMapping("/getuserinfofeign/{id}")
//    @GetMapping("/getuserinfofeign")
    public User getUserCenterByFeign(@PathVariable Integer id) throws IllegalArgumentException {
//    public User getUserCenterByFeign(@RequestParam Integer id, @RequestParam String name) throws IllegalArgumentException {
//        String targetURL = "http://user-center/API/users/{id}";

        // 调用 用户中心 的API，返回用户记录信息
        log.info("通过Feign，调用 微服务user-center");
        User userInfo = this.userCenterFeignClient.findById(id);

        log.info(userInfo.toString());
        return userInfo;
    }


    /**
     * 通过 Feign，调用 微服务user-center（用户中心），并使用多个参数去调用 Get 方法
     * @return User
     */
    @GetMapping("/getuserinfofeign")
    public User getUserCenterByFeign(User user) throws IllegalArgumentException {
//        String targetURL = "http://user-center/API/query";

        // 调用 用户中心 的API，返回用户记录信息
        log.info("通过Feign，调用 微服务user-center");
        User userInfo = this.userCenterFeignClientMultiParam.findByIdAndName(user);

        log.info(userInfo.toString());
        return userInfo;
    }


    /**
     * 通过 Feign，调用 微服务user-center（用户中心），并使用多个参数去调用 Post 方法
     * @return User
     */
    @PostMapping("/postuserinfofeign")
    public User postUserCenterByFeign(@RequestBody User user) throws IllegalArgumentException {
//        String targetURL = "http://user-center/API/post";

        // 调用 用户中心 的API，返回用户记录信息
        log.info("通过Feign，调用 微服务user-center");
        User userInfo = this.userCenterFeignClientMultiParam.postByIdAndName(user);

        log.info(userInfo.toString());
        return userInfo;
    }

    /**
     * Feign 脱离 Nacos 和 Ribbon 也可以使用，需要指定 'url'
     * @return String
     */
    @GetMapping("baidu")
    public String getBaiduIndex() {
        return this.feignClientWithoutNacosAndRibbon.getBaiduIndex();
    }


    /**
     * 测试 Sentinel 限流规则，流控模式：关联
     *
     * Read user info API
     */
    @GetMapping("/readuserinfoAPI")
    public String readUserInfoAPI() {
        log.info("Calling Read User Info API...");
        return "Calling Read User Info API...";
    }


    /**
     * 测试 Sentinel 限流规则，流控模式：关联
     *
     * Write user info API
     */
    @GetMapping("/writeuserinfoAPI")
    public String writeUserInfoAPI() {
        log.info("Calling Write User Info API...");
        return "Calling Write User Info API...";
    }


    /**
     * 测试 Sentinel 限流规则，限流效果：排队等待（匀速排队）
     *
     * @return String
     */
    @GetMapping("test-a")
    public String callTestA() {
        log.info("Call Test A");
        serviceTest.commonAPI();
        return "Call Test A";
    }

    @GetMapping("test-b")
    public String callTestB() {
        log.info("Call Test B");
        serviceTest.commonAPI();
        return "Call Test B";
    }


    /**
     * 测试 Sentinel 限流规则：热点参数 规则
     *
     * 热点就是经常访问的数据；
     * 比如商品接口的 QPS 限定的是 100，有一天要秒杀，
     * 带着秒杀商品 id 的请求的 QPS 限制在 50，
     * 这样还能有 50 的 QPS 用来访问其他的商品；
     *
     * 这就提出了一个请求，要能根据请求的参数来做限流，
     * 比如请求带的参数是热点参数，就对这个请求应用特殊的限流规则；（e.g. /API?a=2)
     * 对携带非热点参数的请求，走另一个限流规则；(e.g. /API?a=其他值）
     *
     * @return String
     */
    @GetMapping("test-hot")
    @SentinelResource("hot")
    public String testHot(
            @RequestParam(required = false) String a,
            @RequestParam(required = false) String b
    ) {
        log.info("test-hot: a = {} b = {}", a, b);
        return a + " " + b;
    }


    /**
     * 使用 代码 配置Sentinel规则
     * 例子：为访问接口"/test/test-a"，配置 流控规则
     *
     * @return String
     */
    @GetMapping("test-add-flow-rule")
    public String testAddFlowRule() {
        log.info("Call test-add-flow-rule");
        this.initFlowQpsRule();
        return "Success";
    }

    // 上面的方法 调用了 initFlowQpsRule()，为 访问接口"/test/test-a" 添加了一条 流控规则
    // 规定阈值是 QPS=20，超过阈值就会限流。
    private void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule("/test/test-a");
        // set limit QPS to 20
//        rule.setCount(20);
        rule.setCount(1);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setLimitApp("default");
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }


    /*
     * Sentinel使用姿势
     * 1，编码方式， 三个核心 API: SphU, Tracer, ContextUtil
     * 2，注解方式，sentinelResource, blockHandler处理blockException, 1.6前的fallback只处理降级，之后的还能处理异常
     * 3，restTemplate 整合 sentinel, SentinelRestTemplate, blockHandler/fallback 与注解方式类似
     * 4，Feign 整合，只需开启feign.sentinel.enabled=true配置，fallback降级逻辑自定义，fallbackFactory除了逻辑再加上异常捕获
     */

    /**
     * ( 1 ) 使用 Sentinel API: SphU, Tracer, ContextUtil
     * @param a
     * @return String
     */
    @GetMapping("/test-sentinel-api")
    public String testSentinelAPI(
            @RequestParam (required = false) String a) {
        // 定义一个 Sentinel 保护的资源，资源名称是 test-sentinel-api
        String resourceName = "test-sentinel-api";
        ContextUtil.enter(resourceName, "test-wfw");

        Entry entry = null;
        try {
            entry = SphU.entry(resourceName);

            // 被保护的 业务逻辑。。。
            log.info("Call business logic");
            log.info("a = {}", a);
            if (StringUtils.isBlank(a)) {
                throw new IllegalArgumentException("a can not be blank");
            }
            return a;
        }
        // 如果被保护的资源 被限流 或 被降级了，就会抛 BlockException
        catch (BlockException e) {
            log.warn("被限流，或被降级了", e);
            return "被限流，或被降级了";
        }
        catch (IllegalArgumentException e2) {
            // 若需要配置 降级规则，需要通过这种方式 记录业务异常
            // 统计 IllegalArgumentException 发生次数 / 发生占比
            log.warn("参数非法！");
            Tracer.trace(e2);
            return "参数非法！";
        }finally {
            if (entry != null) {
                // 退出entry
                entry.exit();
            }
            ContextUtil.exit();
        }
    }


    /**
     * ( 2 ) 使用 Sentinel 注解: @SentinelResource
     * @param a
     * @return
     */
    @GetMapping("/test-sentinel-resource")
    // 定义一个 Sentinel 保护的资源 value，资源名称是 test-sentinel-api
    // 定义一个 异常处理的方法 blockHandler，名称是 blockProcessing, 需要另外实现处理逻辑的代码
    @SentinelResource(
            value = "test-sentinel-resource",
            // 存放blockHandler的类。对应的处理函数必须 static修饰，否则无法解析
            blockHandlerClass = TestControllerBlockHandler.class,
            blockHandler = "blockProcessing",
            // 存放fallback的类。对应的处理函数必须 static修饰，否则无法解析
            fallbackClass = TestControllerFallbackHandler.class,
            fallback = "fallbackProcessing"
    )
    public String testSentinelResourceAnnotation(
            @RequestParam (required = false) String a) {
        // 被保护的 业务逻辑。。。
        log.info("Call business logic");
        log.info("a = {}", a);
        if (StringUtils.isBlank(a)) {
            throw new IllegalArgumentException("a can not be blank");
        }
        return a;
    }



    /**
     * ( 3 ) 使用 RestTemplate，整合 Sentinel，获取 微服务（用户中心）的目标URL地址
     * 方法：只需要在 入口class 添加注解：@SentinelRestTemplate
     *
     * @return User
     */
    @GetMapping("/getuserinfowithsentinel/{id}")
    public User getUserCenterWithSentinel(@PathVariable Integer id) throws IllegalArgumentException {
        String targetURL = "http://user-center/API/users/{id}";

        // 调用 用户中心 的API，返回用户记录信息
//        User userInfo = this.restTemplate.getForObject(
        ResponseEntity<User> userInfo = this.restTemplate.getForEntity(
                targetURL,
                User.class,
                id
        );

        log.info("Status Code: {}", userInfo.getStatusCode());
        log.info(userInfo.getBody().toString());
//        log.info(userInfo.toString());

        return userInfo.getBody();
//        return userInfo;
    }


    /*
     * ( 4 ) 使用 Feign， 整合 Sentinel， 详见 UserCenterFeignClient 类
     */



}
