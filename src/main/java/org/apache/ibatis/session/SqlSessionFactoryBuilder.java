/*
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/**
 * Builds {@link SqlSession} instances.
 *
 * @author Clinton Begin
 */
public class SqlSessionFactoryBuilder {

  public SqlSessionFactory build(Reader reader) {
    return build(reader, null, null);
  }

  public SqlSessionFactory build(Reader reader, String environment) {
    return build(reader, environment, null);
  }

  public SqlSessionFactory build(Reader reader, Properties properties) {
    return build(reader, null, properties);
  }

  public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
    try {
      XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        reader.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }


  /**
   * 用来创建SqlSessionFactory实例，典型的builder链式创建模式
   * 过建造者模式创建一个工厂类，配置解析的过程全部只解析了两种文件。一个是 mybatis-config.xml 全局配置文件。另外就是可能有很多个的 Mapper.xml 文件，也包 括在 Mapper 接口类上面定义的注解
   * @param inputStream
   * @return
   */
  public SqlSessionFactory build(InputStream inputStream) {
    return build(inputStream, null, null);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment) {
    return build(inputStream, environment, null);
  }

  public SqlSessionFactory build(InputStream inputStream, Properties properties) {
    return build(inputStream, null, properties);
  }

  /**
   * 1. mybatis读取全局xml配置文件，解析XML中各个节点元素
   * 2. 将节点元素键值对，设置到Configuration实例的相关变量中
   * 3. 由Configuration实例创建SqlSessionFactory(DefaultSqlSessionFactory实现类)单例对象
   * @param inputStream
   * @param environment
   * @param properties
   * @return
   */
  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
      // 先构建XML文件解析器
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      // 解析得到Configuration对象，这个对象包含了几乎所有的mybatis配置文件中的信息，十分关键。
      // 然后利用Configuration对象构建SqlSessionFactory单例
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        inputStream.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  /**
   * 创建单例的DefaultSqlSessionFactory
   * @param config
   * @return
   */
  public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
  }

}
