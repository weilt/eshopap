# eshopap
这是采用springcloud做的一个电商小项目,多线程，高并发，高可用。使用到的技术栈有springcloud,springboot,eureka,feign,zuul,druid,fastjson,mybatis,mysql,redis-cluster,ehcache，mysql,swagger2等，近期将要重新优化，将增加支付模块与订单模块。并引入spring cloud config 配置中心 ,spring cloud bus 消息总线,spring cloud sleuth 服务链路追踪,Hystrix  断路器监控等。
eureka作为注册中心
采用feign为服务提供者
zuul作为路由，也考虑用gateway,作为比较，有人认为gateway比zuul更好用。
common通用模块，所有通用服务，类放在此模块。
user为用户服务，整个项目都有使用。
category为商品品类服务，category对应的mysql是一个递归表，在这里费了不少功夫，才将redis cluster整合好，不然访问数据库太频繁。
category服务引入了redis 锁。
redis-cluster没有pipeline命令，在生产环境中，需要自已定义中间件进行批量操作
在category模块中，因为并发量相对较少，没有采用多线程。
在product模块中，采用多线程处理各种读请求。controller 收到请求==>将请求扔给异步的读取服务＝=>异步线程去重，将相同的读请求去重==>执行读取，从redis中读，读不到，ehcache，也读不到，mysql，然后更新两级缓存。主线程在sleep20ms后，直接读redis,返回
在这这程中，涉及到请求去重，刷新数据是的时间版本冲突问题，所以，在这里需要引入分布式锁的机制。
近期更新计划，采用zookeeper做分布式锁，近期加上。
storm处理实时热数据。
缓存雪崩后的重建
hystrinx断路器
订单服务
支付宝服务
微信服务
................................



