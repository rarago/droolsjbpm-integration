package org.kie.spring.jbpm;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public abstract class AbstractJbpmSpringTest {

    protected static PoolingDataSource pds;
    protected ClassPathXmlApplicationContext context;

    @BeforeClass
    public static void generalSetup() {
        pds = setupPoolingDataSource();
    }

    @Before
    public void setup() {
        cleanupSingletonSessionId();
        System.setProperty("java.naming.factory.initial", "bitronix.tm.jndi.BitronixInitialContextFactory");
    }
    
    @After
    public void cleanup() {
        if (context != null) {
            context.close();
            context = null;
        }
        System.clearProperty("java.naming.factory.initial");
    }

    @AfterClass
    public static void generalCleanup() { 
        if (pds != null) {
            pds.close();
        }
    }

    protected static PoolingDataSource setupPoolingDataSource() {
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(50);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", "sa");
        pds.getDriverProperties().put("password", "");
        pds.getDriverProperties().put("url", "jdbc:h2:mem:jbpm-db;MVCC=true");
        pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
        pds.init();
        return pds;
    }

    protected static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {

            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {

                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {

                new File(tempDir, file).delete();
            }
        }
    }
}
