
package br.com.cesumar.agasalha.repository;

import br.com.cesumar.agasalha.model.Doacao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DoacaoRepository extends MongoRepository<Doacao, String> {

    boolean existsByDoadorId(String doadorId);

    boolean existsByAbrigoId(String abrigoId);
}
