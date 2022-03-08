package BPCS;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Extraction {
    public static String[] TEXT = { "txt" };
    public static String[] IMAGE = { "png" };

    private Vessel image;
    private double length;
    private int type;
    private int[][] pixels;
    Plane[] planes;
    List<int[][]> blockList = new ArrayList<>();

    public Extraction() {

    }

    public void extract(String filename) throws Exception {
        this.image = new Vessel(filename);
        try {
            pixels = image.readPixels();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.planes = image.getPlanes();

        /* Get complex blocks */

        for (int i = 0; i < 24; i++) {
            for (int b : planes[i].getComplexRegions()) {
                blockList.add(planes[i].getBlocks().get(b).block);
            }
        }

        /* Get meta data */
        int[][] metaBlock = Payload.conjugateBlock(blockList.get(0));
        blockList.remove(0);
        String lengthBits = "", mimeBits = "";
        for (int i = 0; i < 4; i++) {
            lengthBits += bitsToString(metaBlock[i]);
        }
        for (int i = 4; i < 8; i++) {
            mimeBits += bitsToString(metaBlock[i]);
        }
        length = Integer.parseInt(lengthBits, 2);
        double totalDataBlocks = Math.ceil(length / 8.0);
        // double totalConjugationBlocks = Math.ceil(totalDataBlocks / 64.0);

        // List<int[][]> conjugationBlocks = blockList.subList(0, (int) totalConjugationBlocks);
        // List<Integer> map = new ArrayList<>();
        // for (int[][] block : conjugationBlocks) {
        //      int[][] final_block = Payload.conjugateBlock(block);
        //      for(int i = 0; i< 8;i++) for (int j = 0; j< 8;j++) map.add(final_block[i][j]);
        // };
        // map = map.subList(0, (int)totalDataBlocks);
        // blockList = blockList.subList((int)totalConjugationBlocks,(int)totalConjugationBlocks + (int)totalDataBlocks);


        /* Conjugation Data */
        List<int[][]> conjugatedBlocks = new ArrayList<>();
        int conjugationMapSize = (int) Math.floor(totalDataBlocks / 64.0) * 64 + (int) (totalDataBlocks % 64);
        int val = (int) Math.ceil(totalDataBlocks / 64.0); // No of conjugation map blocks
        int index = 0, size = 1;
        while (index < val && size <= conjugationMapSize) {
            int[][] b = Payload.conjugateBlock(blockList.get(index));
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (size <= conjugationMapSize) {
                        if (b[i][j] == 1) {
                            conjugatedBlocks.add(Payload.conjugateBlock(blockList.get(val + size - 1)));
                        } else
                            conjugatedBlocks.add(blockList.get(val + size - 1));
                        size++;
                    } else
                        break;
                }
            }
            index++;
        }
        blockList.removeAll(blockList.subList(0, val)); // Remove conjugation blocks

        /* Extracting data */
        blockList = blockList.subList(0, (int) Math.ceil(length / 8.0)); // Get the blocks which contain data
        type = Integer.parseInt(mimeBits, 2);
        String extension = MimeType.getMimeTypeFromValue(type).getExtension();
        if (extension == null)
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
                  bytes.subList((int)length, bytes.size()).clear();
            // /* Remove the padding */
            // if (length < bytes.size())
            //     bytes = bytes.subList(0, (int)length);

            /* Convert list to array of bytes */
            byte[] byteArray = new byte[bytes.size()];
            int pos = 0;
            for (Byte b : bytes) {
                byteArray[pos++] = b.byteValue();
            }

            try {
                OutputStream output = new FileOutputStream("/Users/arjunagarwal/Desktop/extracted." + extension);
                output.write(byteArray);
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String bitsToString(int[] bits) {
        return Arrays.toString(bits)
                .replace("[", "")
                .replace("]", "")
                .replace(",", "")
                .replace(" ", "");
    }

}
