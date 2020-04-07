package org.kiharu.hareru.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@Configuration
@ConditionalOnProperty(name = "spring.datasource.dbcp2.url", matchIfMissing = false)
// 配置*Mapper.java的扫描路径
@MapperScan(value = {"org.kiharu.hareru.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class DataSourceConfiguration {

    /**
     * 配置数据源
     * @return
     */
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix="spring.datasource.dbcp2")
    public DataSource dataSource() {
        return new BasicDataSource();
    }

    /**
     * 配置数据库事务
     * @param dataSource
     * @return
     */
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager dbOneTransactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 配置会话工厂
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "sqlSessionFactory")
    @ConditionalOnMissingBean(name = "sqlSessionFactory")
    public SqlSessionFactory dbOneSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        // 配置*Mapper.xml的扫描路径
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*Mapper.xml"));
        return sessionFactory.getObject();
    }
}
