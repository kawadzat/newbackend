package io.getarrays.securecapita.Lines;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Transactional
public class LinesService {


    private final LinesRepository linesRepository;

    public LinesService(LinesRepository linesRepository) {
        this.linesRepository = linesRepository;
    }

    public Lines create(Lines lines) {
        return linesRepository.save(lines);
    }

    public List<Lines> findAll() {
        return linesRepository.findAll();
    }

    public long count() {
        return linesRepository.count();
    }



}
