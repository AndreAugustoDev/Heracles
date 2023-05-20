package earth.terrarium.heracles.api.client.settings.tasks;

import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedEntityPredicate;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.client.settings.base.RegistrySetting;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import net.minecraft.Optionull;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class KillEntityTaskSettings implements SettingInitializer<KillEntityQuestTask> {

    public static final KillEntityTaskSettings INSTANCE = new KillEntityTaskSettings();

    @Override
    public CreationData create(@Nullable KillEntityQuestTask object) {
        CreationData settings = new CreationData();
        settings.put("entity", RegistrySetting.ENTITY, Optionull.map(object, task -> task.entity().entityType()));
        settings.put("amount", IntSetting.ONE, Optionull.map(object, KillEntityQuestTask::target));
        return settings;
    }

    @Override
    public KillEntityQuestTask create(String id, KillEntityQuestTask object, Data data) {
        EntityType<?> entityType = data.get("entity", RegistrySetting.ENTITY)
            .orElse(Optionull.mapOrDefault(object, task -> task.entity().entityType(), EntityType.PIG));
        RestrictedEntityPredicate old = Optionull.map(object, KillEntityQuestTask::entity);

        RestrictedEntityPredicate entity = new RestrictedEntityPredicate(
            entityType,
            Optionull.map(old, RestrictedEntityPredicate::location),
            Optionull.map(old, RestrictedEntityPredicate::effects),
            Optionull.map(old, RestrictedEntityPredicate::nbt),
            Optionull.map(old, RestrictedEntityPredicate::flags),
            Optionull.map(old, RestrictedEntityPredicate::targetedEntity)
        );

        return new KillEntityQuestTask(id, entity, data.get("amount", IntSetting.ONE).orElse(1));
    }
}