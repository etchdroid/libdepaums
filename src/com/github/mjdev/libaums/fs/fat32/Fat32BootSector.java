package com.github.mjdev.libaums.fs.fat32;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Fat32BootSector {
	private static final int BYTES_PER_SECTOR_OFF = 11;
	private static final int SECTORS_PER_CLUSTER_OFF = 13;
	private static final int RESERVED_COUNT_OFF = 14;
	private static final int FAT_COUNT_OFF = 16;
	private static final int TOTAL_SECTORS_OFF = 32;
	private static final int SECTORS_PER_FAT_OFF = 36;
	private static final int FLAGS_OFF = 40;
	private static final int ROOT_DIR_CLUSTER_OFF = 44;
	private static final int FS_INFO_SECTOR_OFF = 48;
	private static final int VOLUME_LABEL_OFF = 48;
	
	private short bytesPerSector;
	private byte sectorsPerCluster;
	private short reservedSectors;
	private byte fatCount;
	private long totalNumberOfSectors;
	private long sectorsPerFat;
	private long rootDirStartCluster;
	private short fsInfoStartSector;
	private boolean fatMirrored;
	private byte validFat;
	private String volumeLabel;
	
	private Fat32BootSector() {
		
	}
	
	public static Fat32BootSector read(ByteBuffer buffer) {
		Fat32BootSector result = new Fat32BootSector();
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		result.bytesPerSector = buffer.getShort(BYTES_PER_SECTOR_OFF);
		result.sectorsPerCluster = buffer.get(SECTORS_PER_CLUSTER_OFF);
		result.reservedSectors = buffer.getShort(RESERVED_COUNT_OFF);
		result.fatCount = buffer.get(FAT_COUNT_OFF);
		result.totalNumberOfSectors = buffer.getInt(TOTAL_SECTORS_OFF) & 0xffffffffl;
		result.sectorsPerFat = buffer.getInt(SECTORS_PER_FAT_OFF) & 0xffffffffl;
		result.rootDirStartCluster = buffer.getInt(ROOT_DIR_CLUSTER_OFF) & 0xffffffffl;
		result.fsInfoStartSector = buffer.getShort(FS_INFO_SECTOR_OFF);
		short flag = buffer.getShort(FLAGS_OFF);
		result.fatMirrored = ((byte)flag & 0x80) == 0;
		result.validFat = (byte) ((byte)flag & 0x7);
		
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < 11; i++) {
			byte b = buffer.get(VOLUME_LABEL_OFF + i);
			if(b == 0) break;
			builder.append((char) b);
		}
		
		result.volumeLabel = builder.toString();
		
		return result;
	}

	public short getBytesPerSector() {
		return bytesPerSector;
	}

	public byte getSectorsPerCluster() {
		return sectorsPerCluster;
	}

	public short getReservedSectors() {
		return reservedSectors;
	}

	public byte getFatCount() {
		return fatCount;
	}

	public long getTotalNumberOfSectors() {
		return totalNumberOfSectors;
	}

	public long getSectorsPerFat() {
		return sectorsPerFat;
	}

	public long getRootDirStartCluster() {
		return rootDirStartCluster;
	}

	public short getFsInfoStartSector() {
		return fsInfoStartSector;
	}

	public boolean isFatMirrored() {
		return fatMirrored;
	}

	public byte getValidFat() {
		return validFat;
	}
	
	public int getBytesPerCluster() {
		return sectorsPerCluster * bytesPerSector;
	}
	
	public long getFatOffset(int fatNumber) {
		return getBytesPerSector() * (getReservedSectors() + fatNumber * getSectorsPerFat());
	}
	
	public long getDataAreaOffset() {
		return getFatOffset(0) + getFatCount() * getSectorsPerFat() * getBytesPerSector();
	}
	
	public String getVolumeLabel() {
		return volumeLabel;
	}
}