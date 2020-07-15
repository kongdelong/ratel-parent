package com.ratel;

import com.ratel.modules.demo.domain.DemoDomain;
import com.ratel.modules.demo.repository.DemoRepository;
import com.ratel.modules.demo.service.DemoService;
import com.ratel.modules.demo.service.vo.DemoDomainVo;
import com.ratel.modules.system.domain.QSysUser;
import com.ratel.modules.system.domain.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppRun.class})// 指定启动类
public class DemoQueryTest {


    @Autowired
    DemoService demoService;


    @Autowired
    private DemoRepository demoRepository;

    /**
     * 联合查询
     *
     * @throws Exception
     */
    @Test
    public void findDemo01() throws Exception {
        Long id = new Long(1);
        Pageable pageable = PageRequest.of(1, 2);
        List<DemoDomainVo> list = demoRepository.getDemoDomainVo(id, pageable);
        System.out.println("id: " + id + " | list " + list);
    }

    /**
     * 动态条件查询
     *
     * @throws Exception
     */
    @Test
    public void findDemo02() throws Exception {
        Long id = new Long(1);
        Pageable pageable = PageRequest.of(1, 2);
        List<DemoDomain> list = demoRepository.findUserBySql("username", null, pageable);
        System.out.println("id: " + id + " | list " + list);
    }

    @Test
    public void dslQuery01() {
        QSysUser user = QSysUser.sysUser;
        List<SysUser> list = demoRepository.getJPAQueryFactory().selectFrom(user).fetch();
        System.out.println("user: " + user + " | list " + list);
    }


    /**
     * @throws Exception
     */
    @Test
    public void update01() throws Exception {
        demoRepository.updateById(2L, "username", "999");
    }

    @Test
    public void update02() throws Exception {
        demoRepository.updateStatus(2L);
    }


    /**
     * @throws Exception
     */
    @Test
    public void insert01() throws Exception {

        DemoDomain demoDomain = new DemoDomain();
        demoRepository.save(demoDomain);
    }

}
