import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisSentinelTest {

    private static final Logger logger = LoggerFactory.getLogger(RedisSentinelTest.class);

    public static void main(String[] args) {
        String masterName = "mymaster";
        Set<String> sentinel = new HashSet<String>();
        sentinel.add("127.0.0.1:26379");
        sentinel.add("127.0.0.1:26380");
        sentinel.add("127.0.0.1:26381");
        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(masterName, sentinel);
        int count =0;
        while (true) {
            count++;
            Jedis jedis = null;
            try {
                jedis = jedisSentinelPool.getResource();
                int index = new Random().nextInt(10000);
                String key = "k_" + index;
                String values = "v_" + index;
                jedis.set(key, values);
                if(count%100==0){
                    logger.info("{} values is {}", key, jedis.get(key));
                }
                TimeUnit.MILLISECONDS.sleep(10);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }


    }
}
