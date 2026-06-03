package hexlet.code.app.mapper;

import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ReferenceMapper {
    @PersistenceContext
    private EntityManager entityManager;

    public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> entityClass) {
        return id != null ? entityManager.find(entityClass, id) : null;
    }

    public <T extends BaseEntity> List<T> toEntities(List<Long> ids, @TargetType Class<T> entityClass) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<T> entities = new ArrayList<>();
        for (Long id : ids) {
            T entity = entityManager.find(entityClass, id);
            if (entity == null) {
                throw new ResourceNotFoundException("Entity of type " + entityClass.getSimpleName() + " with ID " + id + " not found");
            }
            entities.add(entity);
        }

        return entities;
    }
}
