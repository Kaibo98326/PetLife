package com.petlife.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.petlife.model.Product;

import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class ProductDao implements IProductDao{
	
	@PersistenceContext
	private Session session;
	
	@Override
	public Product selectById(Integer productId) {
		return session.find(Product.class, productId);
	}
	
	@Override
    public List<Product> selectAll() {
        Query<Product> query = session.createQuery("from Product", Product.class);
        return query.list();
    }

    @Override
    public List<Product> selectByCategory(Integer categoryId, int page, int size) {
        Query<Product> query = session.createQuery(
                "from Product p where p.categoryId = :categoryId order by p.productId desc",
                Product.class);
        query.setParameter("categoryId", categoryId);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    public List<Product> selectByKeyword(String keyword, int page, int size) {
        Query<Product> query = session.createQuery(
                "from Product p where p.productName like :keyword order by p.productId desc",
                Product.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    public String insert(Product product) {
        session.persist(product);
        return "OK";
    }

    @Override
    public Product update(Product product) {
        return session.merge(product);
    }

    @Override
    public boolean deleteById(Integer id) {
        Product product = session.find(Product.class, id);
        if (product != null) {
            session.remove(product);
            return true;
        }
        return false;
    }

    @Override
    public int countAll(String keyword) {
        Query<Long> query = session.createQuery(
                "select count(p) from Product p where p.productName like :keyword", Long.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.uniqueResult().intValue();
    }

    @Override
    public int countByCategory(Integer categoryId) {
        Query<Long> query = session.createQuery(
                "select count(p) from Product p where p.categoryId = :categoryId", Long.class);
        query.setParameter("categoryId", categoryId);
        return query.uniqueResult().intValue();
    }

}
