package foolkey.tool.cache;

import com.alibaba.fastjson.JSON;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import foolkey.pojo.root.CAO.key.KeyCAO;
import foolkey.pojo.root.CAO.userInfo.UserCAO;
import foolkey.pojo.root.vo.assistObject.RSAKeyDTO;
import foolkey.pojo.root.vo.assistObject.TechnicTagEnum;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 出于性能考虑，所有操作不会判token空
 * Created by geyao on 2017/4/25.
 */
@Component("localCache")
public class LocalCache implements Cache{



    private static MemCachedClient memcachedClient = null;

    static {
        //管理中心，点击“NoSQL高速存储”，在NoSQL高速存储“管理视图”，可以看到系统分配的IP:Port
        //需要在内网IP上访问, 不需要账号密码
        final String ip = "10.66.137.15";
        final String port = "9101";

        String[] servers = {ip + ":" + port};

        //创建Socked连接池实例
        SockIOPool pool = SockIOPool.getInstance();
        pool.setServers(servers);//设置连接池可用的cache服务器列表
        pool.setFailover(true);//设置容错开关
        pool.setInitConn(10);//设置开始时每个cache服务器的可用连接数
        pool.setMinConn(5);//设置每个服务器最少可用连接数
        pool.setMaxConn(250);//设置每个服务器最大可用连接数
        pool.setMaintSleep(30);//设置连接池维护线程的睡眠时间
        pool.setNagle(false);//设置是否使用Nagle算法，因为我们的通讯数据量通常都比较大（相对TCP控制数据）而且要求响应及时， <br> 因此该值需要设置为false（默认是true） </br>
        pool.setSocketTO(3000);//设置socket的读取等待超时值
        pool.setAliveCheck(true);//设置连接心跳监测开关
        pool.initialize();

        memcachedClient = new MemCachedClient();
        memcachedClient.delete("key_YY_RsaKeyDTO");
//        System.out.println( "刷新整个缓存能成功吗 ? \n" + memcachedClient.flushAll() );
//        测试用，刷新整个缓存区


    }



    @Override
    public boolean isContainToken(String token) {
        Object obj =  memcachedClient.get(token);
        return obj != null && !obj.toString().equals("");
    }

    @Override
    public String getString(String token) {
        Object result = memcachedClient.get(token);
        return result == null ? null : result.toString();
    }



    @Override
    public void remove(String token) {
        memcachedClient.delete(token);
    }


    @Override
    public void set(String token, String content) {
        memcachedClient.set(token, content);
    }

    @Override
    public void add(String key, String value) {
        memcachedClient.add( key, value );
    }
}
