package hit.cs.sciencefms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hit.cs.sciencefms.entity.IntellectualProperty;
import hit.cs.sciencefms.mapper.IntellectualPropertyMapper;
import hit.cs.sciencefms.service.IntellectualPropertyService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识产权服务实现类
 */
@Service
public class IntellectualPropertyServiceImpl extends ServiceImpl<IntellectualPropertyMapper, IntellectualProperty> implements IntellectualPropertyService {

    @Override
    public List<IntellectualProperty> getPropertiesByTeacherId(Long teacherId) {
        LambdaQueryWrapper<IntellectualProperty> wrapper = Wrappers.<IntellectualProperty>lambdaQuery()
                .eq(IntellectualProperty::getTeacherId, teacherId)
                .orderByDesc(IntellectualProperty::getAuthDate);
        return list(wrapper);
    }

    @Override
    public Page<IntellectualProperty> pagePropertiesByTeacherId(Long teacherId, int page, int pageSize, String type, String keyword) {
        Page<IntellectualProperty> pageParam = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<IntellectualProperty> wrapper = Wrappers.<IntellectualProperty>lambdaQuery()
                .eq(IntellectualProperty::getTeacherId, teacherId);
        
        // 按类型筛选
        if (StringUtils.isNotEmpty(type)) {
            wrapper.eq(IntellectualProperty::getType, type);
        }
        
        // 关键词搜索
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.and(w -> w.like(IntellectualProperty::getTitle, keyword)
                    .or()
                    .like(IntellectualProperty::getAuthNumber, keyword)
                    .or()
                    .like(IntellectualProperty::getSubtype, keyword));
        }
        
        wrapper.orderByDesc(IntellectualProperty::getAuthDate, IntellectualProperty::getApplyDate);
        
        return page(pageParam, wrapper);
    }

    @Override
    public boolean addProperty(IntellectualProperty property) {
        property.setCreateTime(LocalDateTime.now());
        property.setUpdateTime(LocalDateTime.now());
        property.setIsDeleted(0);
        return save(property);
    }

    @Override
    public boolean updateProperty(IntellectualProperty property) {
        property.setUpdateTime(LocalDateTime.now());
        return updateById(property);
    }

    @Override
    public boolean deleteProperty(Long id) {
        return removeById(id);
    }

    @Override
    public IntellectualProperty getPropertyById(Long id) {
        return getById(id);
    }
} 