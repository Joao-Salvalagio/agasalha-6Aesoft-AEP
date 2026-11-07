package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.model.Abrigo;
import br.com.cesumar.agasalha.model.Demanda;
import br.com.cesumar.agasalha.model.Genero;
import br.com.cesumar.agasalha.model.ItemAgasalho;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MatchingService {

    private static final Comparator<SugestaoMatch> PRIORIDADE = Comparator
            .comparingInt((SugestaoMatch s) -> s.demanda().deficit()).reversed()
            .thenComparing(s -> s.abrigo().getId(), Comparator.nullsLast(Comparator.naturalOrder()));

    public Optional<SugestaoMatch> sugerir(ItemAgasalho item, List<Abrigo> abrigos) {
        return abrigos.stream()
                .filter(Abrigo::temEspaco)
                .flatMap(abrigo -> abrigo.getDemandas().stream()
                        .filter(demanda -> compativel(item, demanda))
                        .map(demanda -> new SugestaoMatch(abrigo, demanda)))
                .min(PRIORIDADE);
    }

    private boolean compativel(ItemAgasalho item, Demanda demanda) {
        return demanda.emAberto()
                && item.getTamanho() == demanda.getTamanho()
                && generoCompativel(item.getGenero(), demanda.getGenero());
    }

    private boolean generoCompativel(Genero item, Genero demanda) {
        return item == Genero.UNISSEX || demanda == Genero.UNISSEX || item == demanda;
    }
}
