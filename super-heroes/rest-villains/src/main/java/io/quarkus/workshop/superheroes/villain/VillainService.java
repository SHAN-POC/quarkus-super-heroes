package io.quarkus.workshop.superheroes.villain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
@Transactional(TxType.REQUIRED)
public class VillainService {

    @Inject
    Logger logger;
    @ConfigProperty(name = "level.multiplier", defaultValue="1.0")
    double levelMultiplier;
    @Transactional(TxType.SUPPORTS)
    public List<Villain> findAllVillains() {
        return Villain.listAll();
    }

    @Transactional(TxType.SUPPORTS)
    public Optional<Villain> findVillainById(Long id) {
        return Villain.findByIdOptional(id);
    }

    @Transactional(TxType.SUPPORTS)
    public Villain findRandomVillain() {
        Villain randomVillain = null;
        while (null == randomVillain) {
            randomVillain = Villain.findRandom();
        }
        return randomVillain;
    }

    public Villain persistVillain(@Valid Villain villain) {
        logger.infof("LevelMultiplier = %f", levelMultiplier);
        villain.level = (int) Math.round(villain.level * levelMultiplier);
        villain.persist();
        return villain;
    }

    public Villain updateVillain(@Valid Villain villain) {
        Villain existingEntity = Villain.findById(villain.id);
        existingEntity.name = villain.name;
        existingEntity.level = villain.level;
        existingEntity.picture = villain.picture;
        existingEntity.powers = villain.powers;
        existingEntity.otherName = villain.otherName;
        return existingEntity;
    }

    public void deleteVillain(Long id) {
        Villain villain = Villain.findById(id);
        villain.delete();
    }
}
