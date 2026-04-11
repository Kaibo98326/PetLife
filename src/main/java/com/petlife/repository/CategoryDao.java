package com.petlife.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.petlife.model.Category;

import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class CategoryDao implements ICategoryDao {

	@PersistenceContext
    private Session session;

    @Override
    public Category selectById(Integer categoryId) {
        return session.find(Category.class, categoryId);
    }

    @Override
    public List<Category> selectAll() {
        Query<Category> query = session.createQuery("from Category", Category.class);
        return query.list();
    }

    @Override
    public String insert(Category category) {
        session.persist(category);
        return "OK";
    }

    @Override
    public Category update(Category category) {
        return session.merge(category);
    }

    @Override
    public boolean deleteById(Integer id) {
        Category category = session.find(Category.class, id);
        if (category != null) {
            session.remove(category);
            return true;
        }
        return false;
    }

}
