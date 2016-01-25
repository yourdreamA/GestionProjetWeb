package com.tnt.gestionproj.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.tnt.gestionproj.dao.UserDao;
import com.tnt.gestionproj.entities.Login;
import com.tnt.gestionproj.entities.Projet;
import com.tnt.gestionproj.entities.User;
import com.tnt.gestionproj.enums.TypeUserEnum;
import com.tnt.gestionproj.vo.UserVO;

@Repository
public class UserDaoImpl //extends JpaDaoSupport 
implements UserDao {

	@PersistenceContext(unitName="GestionProjetJPA")
	private EntityManager entityManager;
 
    public List<User> getUsers(TypeUserEnum typeUser, UserVO filter) {
    	StringBuilder queryString = new StringBuilder("from ")
    	.append(typeUser.name()).append(" where 1=1 ");
    	
    	Map<String, Object> params = new HashMap<String, Object>();
    	if (filter != null) {
    		if (filter.getNom() != null && !filter.getNom().isEmpty()) {
    			queryString.append(" and nom = :nom");
    			params.put("nom", filter.getNom());
    		}
    		if (filter.getPrenom() != null && !filter.getPrenom().isEmpty()) {
    			queryString.append(" and prenom = :prenom");
    		}
    		if (filter.getAdresse() != null && !filter.getAdresse().isEmpty()) {
    			queryString.append(" and adresse = :adresse");
    		}
    		if (filter.getTelephone() != null && !filter.getTelephone().isEmpty()) {
    			queryString.append(" and telephone = :telephone");
    		}
    		if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
    			queryString.append(" and email = :email");
    		}
    		if (filter.getDateNaissance() != null) {
    			queryString.append(" and dateNaissance = :dateNaissance");
    		}
    		if (filter.getLogin() != null 
    				&& filter.getLogin().getUsername() != null 
    				&& !filter.getLogin().getUsername().isEmpty()) {
    			queryString.append(" and login.username = :login");
    		}
    	}
    	
        Query query = entityManager.createQuery(queryString.toString());
        
        if (filter != null) {
    		if (filter.getNom() != null && !filter.getNom().isEmpty()) {
    			query.setParameter("nom", filter.getNom());
    		}
    		if (filter.getPrenom() != null && !filter.getPrenom().isEmpty()) {
    			query.setParameter("prenom", filter.getPrenom());
    		}
    		if (filter.getAdresse() != null && !filter.getAdresse().isEmpty()) {
    			query.setParameter("adresse", filter.getAdresse());
    		}
    		if (filter.getTelephone() != null && !filter.getTelephone().isEmpty()) {
    			query.setParameter("telephone", filter.getTelephone());
    		}
    		if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
    			query.setParameter("email", filter.getEmail());
    		}
    		if (filter.getDateNaissance() != null) {
    			query.setParameter("dateNaissance", filter.getDateNaissance());
    		}
    		if (filter.getLogin() != null 
    				&& filter.getLogin().getUsername() != null 
    				&& !filter.getLogin().getUsername().isEmpty()) {
    			query.setParameter("login", filter.getLogin().getUsername());
    		}
        }
        List<User> resultList = query.getResultList();
        return resultList;
    }
    
    

	public void addUser(User user) {
		entityManager.persist(user);
		
	}



	public User searchUserById(long id) {
		return entityManager.find(User.class, id);
	}
	
	public User searchUserByLogin(Login login) {
		User result = null;
		
		try {
			Query query = entityManager.createQuery("from userApp u where u.login.username= :username and u.login.password= :password");
			query.setParameter("username", login.getUsername());
			query.setParameter("password", login.getPassword());
			
			List list = query.getResultList();
			
			if (list != null && !list.isEmpty()) {
				result = (User) list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return result;
	}



	public void updateUser(User user) {
		entityManager.merge(user);
		
	}



	@Override
	public List<Login> getAllLogin() {
    	StringBuilder queryString = new StringBuilder("from Login");
    	
        Query query = entityManager.createQuery(queryString.toString());
        List<Login> resultList = query.getResultList();
        
        return resultList;
    }
	
	@Override
	public Login searchLogin(String userName) {
		Login res = null;
    	StringBuilder queryString = new StringBuilder("from Login where username='" + userName + "'");
    	
        Query query = entityManager.createQuery(queryString.toString());
        List<Login> resultList = query.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
        	res = resultList.get(0);
        }
        
        return res;
    }
	
	@Override
	public List<User> getAllUser() {
    	StringBuilder queryString = new StringBuilder("from userApp");
    	
        Query query = entityManager.createQuery(queryString.toString());
        List<User> resultList = query.getResultList();
        
        return resultList;
    }

}
