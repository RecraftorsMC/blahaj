package mc.recraftors.blahaj;

import mc.recraftors.blahaj.item.CuddlyContainerTooltipComponent;
import mc.recraftors.blahaj.item.CuddlyContainerTooltipData;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class BlahajForgeClient {
    private BlahajForgeClient() {}

    static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(BlahajForgeClient::tooltipFactoryEventHandler);
    }

    private static void tooltipFactoryEventHandler(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CuddlyContainerTooltipData.class, CuddlyContainerTooltipComponent::new);
    }
}
