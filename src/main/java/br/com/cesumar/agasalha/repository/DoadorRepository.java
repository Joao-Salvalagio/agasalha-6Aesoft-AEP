
package br.com.cesumar.agasalha.repository;

import br.com.cesumar.agasalha.model.Doador;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DoadorRepository extends MongoRepository<Doador, String> {
}
