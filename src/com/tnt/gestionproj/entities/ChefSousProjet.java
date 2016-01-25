package com.tnt.gestionproj.entities;

import com.tnt.gestionproj.entities.User;
import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: ChefSousProjet
 *
 */
@Entity

public class ChefSousProjet extends User implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public ChefSousProjet() {
		super();
	}
   
}
