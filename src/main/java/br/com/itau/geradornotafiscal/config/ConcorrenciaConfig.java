package br.com.itau.geradornotafiscal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ConcorrenciaConfig {

    @Bean(destroyMethod = "close")
    ExecutorService integracoesExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
