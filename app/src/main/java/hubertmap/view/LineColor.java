package hubertmap.view;

/** Stores line color values */
public class LineColor {
    /**
     * Returns color for given line
     *
     * @param line number
     * @return string color value
     */
    public static String getColor(String line) {
        switch (line) {
            case "1":
                return "#FFCE00";
            case "2":
                return "#0064B0";
            case "3":
                return "#9F9825";
            case "3B":
                return "#98D4E2";
            case "4":
                return "#C04191";
            case "5":
                return "#F28E42";
            case "6":
                return "#83C491";
            case "7":
                return "#F3A4BA";
            case "7B":
                return "#83C491";
            case "8":
                return "#CEADD2";
            case "9":
                return "#D5C900";
            case "10":
                return "#E3B32A";
            case "11":
                return "#8D5E2A";
            case "12":
                return "#00814F";
            case "13":
                return "#98D4E2";
            case "14":
                return "#662483";
            case "15":
                return "#B90845";
            case "16":
                return "#F3A4BA";
            case "17":
                return "#D5C900";
            case "18":
                return "#00A88F";
            default:
                return "#000000";
        }
    }
}
