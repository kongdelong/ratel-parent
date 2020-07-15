package com.ratel.modules.monitor.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.ratel.framework.service.BaseService;
import com.ratel.framework.utils.QueryHelp;
import com.ratel.framework.utils.ValidationUtil;
import com.ratel.modules.monitor.domain.MonitorServer;
import com.ratel.modules.monitor.repository.MonitorServerRepository;
import com.ratel.modules.monitor.service.dto.MonitorServerQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MonitorServerService extends BaseService<MonitorServer, Integer> {

    @Autowired
    private MonitorServerRepository monitorServerRepository;

    public Page queryAll(MonitorServerQueryCriteria criteria, Pageable pageable) {
        Page<MonitorServer> page = monitorServerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        page.forEach(server -> {
            try {
                server.setState("1");
                String url = String.format("http://%s:%d/api/serverMonitor", server.getAddress(), server.getPort());
                String res = HttpUtil.get(url, 1000);
                JSONObject obj = JSONObject.parseObject(res);
                server.setCpuRate(obj.getDouble("cpuRate"));
                server.setCpuCore(obj.getInteger("cpuCore"));
                server.setMemTotal(obj.getDouble("memTotal"));
                server.setMemUsed(obj.getDouble("memUsed"));
                server.setDiskTotal(obj.getDouble("diskTotal"));
                server.setDiskUsed(obj.getDouble("diskUsed"));
                server.setSwapTotal(obj.getDouble("swapTotal"));
                server.setSwapUsed(obj.getDouble("swapUsed"));
            } catch (Exception e) {
                server.setState("0");
                e.printStackTrace();
            }
        });

        return new PageImpl(page.getContent(), pageable, page.getTotalElements());
    }

    public List<MonitorServer> queryAll(MonitorServerQueryCriteria criteria) {
        return monitorServerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    }

    public MonitorServer findById(Integer id) {
        MonitorServer server = monitorServerRepository.findById(id).orElseGet(MonitorServer::new);
        ValidationUtil.isNull(server.getId(), "Server", "id", id);
        return server;
    }

    @Transactional(rollbackFor = Exception.class)
    public MonitorServer create(MonitorServer resources) {
        return monitorServerRepository.save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(MonitorServer resources) {
        MonitorServer server = monitorServerRepository.findById(resources.getId()).orElseGet(MonitorServer::new);
        ValidationUtil.isNull(server.getId(), "Server", "id", resources.getId());
        server.copy(resources);
        monitorServerRepository.save(server);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Integer> ids) {
        for (Integer id : ids) {
            monitorServerRepository.deleteById(id);
        }
    }

}
