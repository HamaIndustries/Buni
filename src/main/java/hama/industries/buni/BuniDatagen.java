package hama.industries.buni;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BuniDatagen {

    public static void generateData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        event.addProvider(
                new BuniItemTagsProvider(
                        packOutput,
                        event.getLookupProvider(),
                        CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()),
                        BuniMod.MODID,
                        event.getExistingFileHelper()
                ));

        event.addProvider(
                new BuniEntityTypeTagsProvider(
                        packOutput,
                        event.getLookupProvider(),
                        BuniMod.MODID,
                        event.getExistingFileHelper()
                ));

        event.addProvider(
                new BuniDamageTypeTagsProvider(
                        packOutput,
                        event.getLookupProvider(),
                        BuniMod.MODID,
                        event.getExistingFileHelper()
                ));

        event.addProvider(new BuniItemModelsProvider(packOutput, event.getExistingFileHelper()));
        event.addProvider(new BuniLanguageProvider(packOutput));
    }

    public static class BuniItemTagsProvider extends ItemTagsProvider {

        public BuniItemTagsProvider(PackOutput p_275204_, CompletableFuture<HolderLookup.Provider> p_275194_, CompletableFuture<TagLookup<Block>> p_275634_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(p_275204_, p_275194_, p_275634_, modId, existingFileHelper);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void addTags(HolderLookup.Provider lookupProvider) {
            this.tag(BuniTags.Items.BUNI_TEMPTATIONS)
                    .addTags(ItemTags.FLOWERS, Tags.Items.CROPS_CARROT, Tags.Items.CROPS_BEETROOT)
                    .add(
                            Items.FERN,
                            Items.LARGE_FERN,
                            Items.SHORT_GRASS,
                            Items.TALL_GRASS,
                            Items.SEAGRASS
                    );
        }
    }

    public static class BuniEntityTypeTagsProvider extends EntityTypeTagsProvider {

        public BuniEntityTypeTagsProvider(PackOutput p_275204_, CompletableFuture<HolderLookup.Provider> p_275194_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(p_275204_, p_275194_, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookupProvider) {
            List<EntityType<?>> hostiles = BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> type.getCategory() == MobCategory.MONSTER).collect(Collectors.toList());
            hostiles.add(EntityType.PLAYER);
            hostiles.add(BuniRegistry.BUNI.get());
            tag(BuniTags.EntityTypes.BUNI_ATTACK).add(hostiles.toArray(EntityType[]::new));
            tag(BuniTags.EntityTypes.KILL_ON_SIGHT);//.add(EntityType.COW);
        }
    }

    public static class BuniItemModelsProvider extends ItemModelProvider {
        public BuniItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, BuniMod.MODID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            for (var item : BuniRegistry.BUNI_ITEMS.entrySet()) {
                basicItem(item.getValue().get());
            }
        }
    }

    public static class BuniDamageTypeTagsProvider extends DamageTypeTagsProvider {
        public BuniDamageTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(pOutput, pLookupProvider, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            tag(BuniTags.DamageTypes.BUNI_IMMUNE)
                    .add(DamageTypes.FALL)
                    .add(DamageTypes.IN_FIRE)
                    .addTags(DamageTypeTags.IS_FIRE);
        }
    }

    public static class BuniLanguageProvider extends LanguageProvider {
        public BuniLanguageProvider(PackOutput output) {
            super(output, BuniMod.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            addEntityType(BuniRegistry.BUNI, "Buni");
            addEntityType(BuniRegistry.BUNBARIAN, "Buni");
            BuniRegistry.BUNI_ITEMS.forEach((s, itemItemDeferredHolder) ->
                    addItem(itemItemDeferredHolder, StringUtils.capitaliseAllWords(s)+" Buni"));

            add("buni.entity.buni.hit", "Buni Hit");
            add("buni.entity.buni.death", "Buni Explodes");
            add("buni.entity.buni.idle", "Buni Chirps");
            add("buni.entity.buni.Guzzle", "Buni Guzzles");
            add("buni.entity.buni.Attack", "Buni Attack");
        }

        public static String getNameFromItem(String s) {
            return StringUtils.capitaliseAllWords(s.split("\\.")[2].replace("_", " "));
        }
    }
}
