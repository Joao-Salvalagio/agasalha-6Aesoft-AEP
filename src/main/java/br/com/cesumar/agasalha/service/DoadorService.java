
package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.controller.dto.DoadorCreateRequest;
import br.com.cesumar.agasalha.controller.dto.DoadorResponse;
import br.com.cesumar.agasalha.controller.dto.DoadorUpdateRequest;
import br.com.cesumar.agasalha.exception.DoadorNaoEncontradoException;
import br.com.cesumar.agasalha.exception.RegistroEmUsoException;
import br.com.cesumar.agasalha.mapper.DoadorMapper;
import br.com.cesumar.agasalha.model.Doador;
import br.com.cesumar.agasalha.repository.DoacaoRepository;
import br.com.cesumar.agasalha.repository.DoadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoadorService {

    private final DoadorRepository repository;
    private final DoacaoRepository doacaoRepository;
    private final DoadorMapper mapper;

    public DoadorResponse criar(DoadorCreateRequest req) {
        Doador novo = mapper.paraModel(req);
        novo.validar();
        return mapper.paraResponse(repository.save(novo));
    }

    public DoadorResponse buscarPorId(String id) {
        return mapper.paraResponse(carregar(id));
    }

    public List<DoadorResponse> listar() {
        return repository.findAll().stream().map(mapper::paraResponse).toList();
    }

    public DoadorResponse atualizar(String id, DoadorUpdateRequest req) {
        Doador existente = carregar(id);
        mapper.aplicar(req, existente);
        existente.validar();
        return mapper.paraResponse(repository.save(existente));
    }

    public void remover(String id) {
        if (!repository.existsById(id)) {
            throw new DoadorNaoEncontradoException(id);
        }
        if (doacaoRepository.existsByDoadorId(id)) {
            throw new RegistroEmUsoException("Doador", id);
        }
        repository.deleteById(id);
    }

    private Doador carregar(String id) {
        return repository.findById(id).orElseThrow(() -> new DoadorNaoEncontradoException(id));
    }
}
