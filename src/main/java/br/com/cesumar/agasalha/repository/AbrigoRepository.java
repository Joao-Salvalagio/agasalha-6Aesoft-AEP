
package br.com.cesumar.agasalha.repository;

import br.com.cesumar.agasalha.model.Abrigo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AbrigoRepository extends MongoRepository<Abrigo, String> {
}
