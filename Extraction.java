package BPCS;

import java.io.*;
import java.util.*;

public class Extraction {
    private static String text = "txt";
    private static String image1 = "png";

    private Image image;
    private enum mimeType {
        TEXT(1, text),
        IMAGE1(2, image1);

        private final int value;
        private final String extension;

        mimeType(int value, String extension) {
            this.value = value;
            this.extension = extension;
        }

        private static final Map<Integer, String> typeCodes = new HashMap<Integer, String>();

        static {
            for (mimeType m : mimeType.values()) {
                typeCodes.put(m.value, m.extension);
            }
        }

    };
    private int length;
    private int type;
    private int[][] pixels;
    Plane[] planes;
    List<int[][]> blockList = new ArrayList<>();


    public Extraction() {

    }

    public void extract(String filename) throws IOException {
        this.image = new Image(filename);
        try {
            pixels = image.readPixels();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.planes = image.getPlanes();

        /* Get complex blocks and check for the padding */
        for(int i = 0; i< 24; i++) {
            for (int b : planes[i].getComplexRegions()) {
                blockList.add(planes[i].getBlocks().get(b).block);
            }
        }

        /* Get meta data */
        int[][] metaBlock = blockList.get(0);
        blockList.remove(0);
        String lengthBits = "", mimeBits = "";
        for(int i=0; i<4; i++) {
             lengthBits += bitsToString(metaBlock[i]);
        }
        for(int i=4; i<8; i++) {
            mimeBits += bitsToString(metaBlock[i]);
        }
        length = Integer.parseInt(lengthBits,2);
        blockList.subList(0, (int) Math.ceil(length/8)); // Get the blocks which contain data
        type = Integer.parseInt(mimeBits, 2);
        String extension = mimeType.typeCodes.get(type);
        if(extension == null)
            System.out.println("Mime type not found");
        else {
            /* Convert the blocks to bytes */
            List<Byte> bytes = new ArrayList<>();
            for (int[][] block : blockList) {
                for (int i = 0; i < 8; i++) {
                    bytes.add((byte) Integer.parseInt(bitsToString(block[i]), 2));
                }
            }
            /* Remove the padding */
            if(length < bytes.size())
                  bytes.subList(length-1, bytes.size()).clear();

            /* Convert list to array of bytes */
            byte[] byteArray = new byte[bytes.size()];
            int pos = 0;
            for (Byte b : bytes) {
                byteArray[pos++] = b.byteValue();
            }

            try {
                OutputStream output = new FileOutputStream("output/extracted."+extension);
                output.write(byteArray);
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String bitsToString(int[] bits) {
       return  Arrays.toString(bits)
                .replace("[", "")
                .replace("]","")
                .replace(",","")
                .replace(" ","");
    }

}

