package com.tnt.gestionproj.entities;

import com.tnt.gestionproj.entities.User;
import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: ChefProjet
 *
 */
@Entity

public class ChefProjet extends User implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public ChefProjet() {
		super();
	}
   
}
