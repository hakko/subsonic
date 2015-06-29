package net.sourceforge.subsonic.ajax;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.service.SettingsService;

import org.jfree.util.Log;

public class DeviceLocatorService {
	
	private static final Logger LOG = Logger
			.getLogger(DeviceLocatorService.class);

	private SettingsService settingsService;

	public Map<String, String> getDeviceMap() {
		Map<String, String> devices = new HashMap<String, String>();
		try {
			UsbServices services = UsbHostManager.getUsbServices();
			UsbHub rootHub = services.getRootUsbHub();

			getDeviceMap(devices, rootHub);
		} catch (Exception e) {
			Log.error("Unable to read USB device map", e);
		}
		LOG.debug(devices);
		return devices;

	}

	public static void getDeviceMap(Map<String, String> devices,
			UsbDevice device) throws Exception {

		UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
		if (device.isUsbHub()) {
			UsbHub hub = (UsbHub) device;
			for (UsbDevice child : (List<UsbDevice>) hub
					.getAttachedUsbDevices()) {
				getDeviceMap(devices, child);
			}
		} else {
			try {
				if(device.getSerialNumberString() != null) {
					devices.put(device.getSerialNumberString(),
							device.getProductString());
				}
			} catch (Exception e) {

			}
		}
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}
	
	public static void main(String ... args) {
		
		DeviceLocatorService service = new DeviceLocatorService();
		System.err.println(service.getDeviceMap());
	}

	
}