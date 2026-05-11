package io.github.m3t4f1v3.permafrost;

import com.mojang.logging.LogUtils;
import io.github.m3t4f1v3.permafrost.block.PermafrostIceBlock;
import io.github.m3t4f1v3.permafrost.integration.ArsNouveauIntegration;
import io.github.m3t4f1v3.permafrost.integration.ThermodynamicaIntegration;
import com.Tribulla.thermodynamica.api.HeatTier;
import io.github.m3t4f1v3.permafrost.mixin.IceBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Permafrost.MODID)
public class Permafrost {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "permafrost";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under
    // the "permafrost" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under
    // the "permafrost" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be
    // registered under the "permafrost" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, MODID);

    // Unbreakable Permafrost Ice Block
    // Only meltable by Power Grid heating coils or Ars Nouveau's Melt Eternal spell
    public static final RegistryObject<Block> PERMAFROST_ICE = BLOCKS.register("permafrost_ice",
            () -> new PermafrostIceBlock(
                    BlockBehaviour.Properties
                            .copy(Blocks.ICE)
                            .mapColor(MapColor.ICE)
                            .strength(-1.0f, 3600000.0f) // Unbreakable (creative only)
                            .noOcclusion()));
    // BlockItem for Permafrost Ice (can only be obtained in creative mode)
    public static final RegistryObject<Item> PERMAFROST_ICE_ITEM = ITEMS.register("permafrost_ice",
            () -> new BlockItem(PERMAFROST_ICE.get(), new Item.Properties()));

    public Permafrost() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ArsNouveauIntegration.register();

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }

    // Add the example block item and permafrost ice to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(PERMAFROST_ICE_ITEM);
        }
    }

    public static void melt(BlockPos sourcePos, BlockState state, Level level, BlockPos pos) {
        Block block = state.getBlock();
        float permafrostThreshold = ThermodynamicaIntegration.getTierCelsius(HeatTier.POS5);
        float iceThreshold = ThermodynamicaIntegration.getTierCelsius(HeatTier.POS4);
        float packedIceThreshold = ThermodynamicaIntegration.getTierCelsius(HeatTier.POS3);
        float snowThreshold = ThermodynamicaIntegration.getTierCelsius(HeatTier.ZERO);
        float powderSnowThreshold = ThermodynamicaIntegration.getTierCelsius(HeatTier.POS1);

        float temperature = ThermodynamicaIntegration.getVisualTemperature(level, pos);
        if (block instanceof PermafrostIceBlock iceBlock) {

            if (temperature >= permafrostThreshold) {
                ((IceBlockAccessor) iceBlock).invokeMelt(state, level, pos);
            }

        } else if (block instanceof IceBlock iceBlock && temperature > iceThreshold) {

            ((IceBlockAccessor) iceBlock).invokeMelt(state, level, pos);

        } else if ((state.is(Blocks.PACKED_ICE) && temperature > packedIceThreshold)
                || (state.is(Blocks.BLUE_ICE) && temperature > iceThreshold)) {

            level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());

        } else if (block instanceof PowderSnowBlock && temperature > powderSnowThreshold) {

            level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());

        } else if (block instanceof SnowLayerBlock && temperature > snowThreshold) {

            level.setBlockAndUpdate(pos,
                    Blocks.WATER.defaultBlockState()
                            .setValue(LiquidBlock.LEVEL,
                                    state.getValue(SnowLayerBlock.LAYERS)));

        } else if (state.is(Blocks.SNOW_BLOCK) && temperature > powderSnowThreshold) {

            level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
        }
    }
}
