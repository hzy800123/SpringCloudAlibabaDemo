package com.example.demo.rocketmq;

import com.example.demo.contentcenter.dao.message.RocketmqTransactionLogMapper;
import com.example.demo.contentcenter.dao.user.UserMapper;
import com.example.demo.contentcenter.domain.entity.message.RocketmqTransactionLog;
import com.example.demo.contentcenter.domain.entity.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RocketMQTransactionListener(txProducerGroup = "tx-add-user-group")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AddUserTransactionListener implements RocketMQLocalTransactionListener {

    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        MessageHeaders headers = msg.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);

        try {
            log.info("Processing local transaction logic here ...");
            createUserWithRocketMqLog(transactionId);

            log.info("Process local transaction logic successfully ! Need to Commit !");
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            log.warn("Process local transaction hit Exception ! Need to Fallback !");
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        log.info("Need to check the local transaction status...");
        MessageHeaders headers = msg.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);

        // 回查 RocketmqTransactionLog 表的记录，确定 本地事务 是否已经提交成功。
        // Select * from xxx where transaction_id = xxx
        RocketmqTransactionLog rocketmqTransactionLog = this.rocketmqTransactionLogMapper.selectOne(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .build()
        );

        if (rocketmqTransactionLog != null) {
            log.info("Process local transaction logic successfully ! Need to Commit !");
            return RocketMQLocalTransactionState.COMMIT;
        }

        log.warn("Process local transaction hit Exception ! Need to Fallback !");
        return RocketMQLocalTransactionState.ROLLBACK;
    }

    // 处理本地事务，并写入RocketMQ Transaction Log表
    @Transactional(rollbackFor = Exception.class)
    private void createUserWithRocketMqLog(String transactionId) throws Exception {
        log.info("Process to create a new User record ...");
        userMapper.insertSelective(
                User.builder()
                .userName("张三2")
                .role("admin")
                .createTime(new Date())
                .updateTime(new Date())
                .build()
        );

        log.info("Insert a new record in RocketMQ Transaction Log Table ...");
        rocketmqTransactionLogMapper.insertSelective(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
//                        .log("Create a new User")
                        .log("创建新用户")
                        .build()
        );

//        throw new Exception("Local Transaction Exception !");
    }
}
