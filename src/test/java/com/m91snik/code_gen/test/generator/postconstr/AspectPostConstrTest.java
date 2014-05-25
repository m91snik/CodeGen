package com.m91snik.code_gen.test.generator.postconstr;

import com.m91snik.code_gen.test.generator.postconstr.impl.PcRecalc;
import com.m91snik.code_gen.test.generator.postconstr.impl.PcTarget;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by m91snik on 23.05.14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/services/generator/post-constr-test-root.xml"})
public class AspectPostConstrTest {

    @Autowired
    private PcTarget pcTarget;

    @Test
    public void testPostConstr() throws Exception {
        Assert.assertEquals(2, pcTarget.getA());
    }

}
