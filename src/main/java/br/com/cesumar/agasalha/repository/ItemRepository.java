package br.com.cesumar.agasalha.repository;

import br.com.cesumar.agasalha.model.ItemAgasalho;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<ItemAgasalho, String> {
}
