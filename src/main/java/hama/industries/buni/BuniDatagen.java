package hama.industries.buni;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BuniDatagen {

    public static void generateData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<BuniItemTagsProvider>) (PackOutput output) -> new BuniItemTagsProvider(
                        output,
                        event.getLookupProvider(),
                        CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()),
                        BuniMod.MODID,
                        event.getExistingFileHelper()
        ));

        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<BuniItemModelsProvider>) (PackOutput output) -> new BuniItemModelsProvider(output, event.getExistingFileHelper())
        );
    }

    public static class BuniItemTagsProvider extends ItemTagsProvider {

        public BuniItemTagsProvider(PackOutput p_275204_, CompletableFuture<HolderLookup.Provider> p_275194_, CompletableFuture<TagLookup<Block>> p_275634_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(p_275204_, p_275194_, p_275634_, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookupProvider) {
            this.tag(BuniTags.Items.BUNI_TEMPTATIONS)
                    .addTags(ItemTags.FLOWERS, Tags.Items.CROPS_CARROT, Tags.Items.CROPS_BEETROOT)
                    .add(
                            Items.FERN,
                            Items.LARGE_FERN,
                            Items.GRASS,
                            Items.TALL_GRASS,
                            Items.SEAGRASS
                    );
        }
    }

    public static class BuniItemModelsProvider extends ItemModelProvider {
        public BuniItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, BuniMod.MODID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            basicItem(BuniRegistry.BUNI_ITEM.get());
        }
    }
}
