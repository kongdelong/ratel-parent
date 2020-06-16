package com.ratel.framework.domain.interceptor.query;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;

public class JpqlBeanPostProcessor implements RepositoryProxyPostProcessor {

    @Override
    public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
        factory.addAdvice(new JpqlBeanMethodInterceptor(repositoryInformation));
    }
}
