package com.jaeckel.locator



import javax.persistence.*
import com.google.appengine.api.datastore.Text;
// import com.google.appengine.api.datastore.Key;

@Entity
class User implements Serializable {

  @Id
  @GeneratedValue (strategy = GenerationType.IDENTITY)
  Long id

  static constraints = {
    id(visible: false)
    name(size: 1..100, blank: false, unique: true)
  }

  String name
  String email
  String passwordHash
  Text pubKey
  String pubKeyId

  public String toString() {
     return "User{" +
             "id=" + id +
             ", name='" + name + '\'' +
             ", email='" + email + '\'' +
             ", passwordHash='" + passwordHash + '\'' +
             ", pubKey=" + pubKey +
             ", pubKeyId='" + pubKeyId + '\'' +
             '}';
   }

  
}
