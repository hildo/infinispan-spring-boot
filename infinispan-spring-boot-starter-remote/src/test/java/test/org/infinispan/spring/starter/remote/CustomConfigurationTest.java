package test.org.infinispan.spring.starter.remote;

import org.infinispan.spring.starter.remote.InfinispanRemoteAutoConfiguration;
import org.infinispan.spring.starter.remote.InfinispanRemoteCacheCustomizer;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    classes = {
        CustomConfigurationTest.TestConfiguration.class,
        InfinispanRemoteAutoConfiguration.class
    },
    properties = {
        "spring.main.banner-mode=off"
    }
)
public class CustomConfigurationTest {
   @Autowired
   private RemoteCacheManager manager;

   @Test
   public void testConfiguredClient() {
      assertThat(manager.getConfiguration().servers().get(0).port()).isEqualTo(6667);
      assertThat(manager.getConfiguration().tcpNoDelay()).isFalse();
      assertThat(manager.getConfiguration().tcpKeepAlive()).isFalse();
   }

   @Configuration
   static class TestConfiguration {
      @Bean
      public org.infinispan.client.hotrod.configuration.Configuration customConfiguration() {
         return new ConfigurationBuilder()
             .addServers("127.0.0.1:6667")
             .tcpNoDelay(false)
             .tcpKeepAlive(true)
             .build();
      }

      @Order(Ordered.HIGHEST_PRECEDENCE)
      @Bean
      public InfinispanRemoteCacheCustomizer customizer() {
         return b -> b.tcpKeepAlive(false);
      }
   }
}
