package com.tnt.gestionproj.entities;

import com.tnt.gestionproj.entities.User;
import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Administrateur
 *
 */
@Entity

public class Administrateur extends User implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public Administrateur() {
		super();
	}
   
}
