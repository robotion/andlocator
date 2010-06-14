package com.jaeckel.locator



import javax.persistence.*
import com.google.appengine.api.datastore.Text;
// import com.google.appengine.api.datastore.Key;

@Entity
class User implements Serializable {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id

    static constraints = {
    	id visible:false
	}

  String name
  String email
  String passwordHash
  Text pubKey
  String pubKeyId

}
