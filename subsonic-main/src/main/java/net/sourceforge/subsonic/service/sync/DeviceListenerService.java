package net.sourceforge.subsonic.service.sync;

import java.util.ArrayList;
import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbHostManager;
import javax.usb.UsbServices;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.SettingsService;

import com.github.hakko.musiccabinet.service.LibraryBrowserService;

public class DeviceListenerService implements UsbServicesListener {

	private static final Logger LOG = Logger
			.getLogger(DeviceListenerService.class);

	private List<UsbDevice> attachedDevices = new ArrayList<UsbDevice>();
	private List<String> serialNumbers;

	private SettingsService settingsService;
	private LibraryBrowserService libraryBrowserService;
	

	public SettingsService getSettingsService() {
		return settingsService;
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	public void init() throws Exception {
		UsbServices services = UsbHostManager.getUsbServices();
		services.addUsbServicesListener(new DeviceListenerService());

		serialNumbers = settingsService.getAllUserDevices();

		new DeviceSyncThread(this);
		LOG.info("Initialized Device Listeners " + serialNumbers);
	}

	public void addSerialNumber(String serial) {
		if (serialNumbers.contains(serial)) {
			return;
		}
		serialNumbers.add(serial);
	}

	public List<String> getSerialNumbers() {
		return serialNumbers;
	}

	public UserSettings getUserSettings(String serial) {
		return settingsService.getUserSettingsByDevice(serial);
	}

	@Override
	public void usbDeviceAttached(UsbServicesEvent event) {
		UsbDevice device = event.getUsbDevice();
		try {
			if (device != null && device.getSerialNumberString() != null) {
				if (serialNumbers.contains(device.getSerialNumberString())) {
					LOG.debug("Device was plugged in.");
					attachedDevices.add(device);
				}
			}
		} catch (Exception e) {
			// LOG.error(
			// "Exception while attempting to read from attached usb device.",
			// e);
		}
	}

	@Override
	public void usbDeviceDetached(UsbServicesEvent event) {
		UsbDevice device = event.getUsbDevice();
		try {
			if (attachedDevices.contains(device)) {
				LOG.debug("Device was unplugged");
				attachedDevices.remove(device);
			}
		} catch (Exception e) {
			// LOG.error(
			// "Exception while attempting to read from detached usb device.",
			// e);
		}
	}

	public LibraryBrowserService getLibraryBrowserService() {
		return libraryBrowserService;
	}

	public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}

}
