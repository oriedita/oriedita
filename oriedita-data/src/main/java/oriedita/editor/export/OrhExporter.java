package oriedita.editor.export;

import jakarta.enterprise.context.ApplicationScoped;
import org.tinylog.Logger;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.export.api.FileExporter;
import oriedita.editor.save.Save;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineSegment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Import and Export Orihime files.
 */
@ApplicationScoped
public class OrhExporter implements FileExporter {
    @Override
    public void doExport(Save save, File file) throws IOException {
        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw); PrintWriter pw = new PrintWriter(bw)) {
            pw.println("<タイトル>");
            pw.println("タイトル," + save.getTitle());

            pw.println("<線分集合>");

            int index = 1;
            for (LineSegment s : save.getLineSegments()) {
                pw.println("番号," + index++);
                pw.println("色," + s.getColor());

                pw.println("<tpp>" + s.getCustomized() + "</tpp>");
                pw.println("<tpp_color_R>" + s.getCustomizedColor().getRed() + "</tpp_color_R>");
                pw.println("<tpp_color_G>" + s.getCustomizedColor().getGreen() + "</tpp_color_G>");
                pw.println("<tpp_color_B>" + s.getCustomizedColor().getBlue() + "</tpp_color_B>");

                pw.println("座標," + s.determineAX() + "," + s.determineAY() + "," + s.determineBX() + "," + s.determineBY());
            }

            pw.println("<円集合>");
            index = 1;
            for (Circle circle : save.getCircles()) {
                pw.println("番号," + index++);
                Circle e_temp = new Circle();
                e_temp.set(circle);
                pw.println("中心と半径と色," + e_temp.getX() + "," + e_temp.getY() + "," + e_temp.getR() + "," + e_temp.getColor());

                pw.println("<tpp>" + e_temp.getCustomized() + "</tpp>");
                pw.println("<tpp_color_R>" + e_temp.getCustomizedColor().getRed() + "</tpp_color_R>");
                pw.println("<tpp_color_G>" + e_temp.getCustomizedColor().getGreen() + "</tpp_color_G>");
                pw.println("<tpp_color_B>" + e_temp.getCustomizedColor().getBlue() + "</tpp_color_B>");
            }

            pw.println("<補助線分集合>");

            index = 1;
            for (LineSegment s : save.getAuxLineSegments()) {
                pw.println("補助番号," + index++);
                pw.println("補助色," + s.getColor());

                pw.println("<tpp>" + s.getCustomized() + "</tpp>");
                pw.println("<tpp_color_R>" + s.getCustomizedColor().getRed() + "</tpp_color_R>");
                pw.println("<tpp_color_G>" + s.getCustomizedColor().getGreen() + "</tpp_color_G>");
                pw.println("<tpp_color_B>" + s.getCustomizedColor().getBlue() + "</tpp_color_B>");

                pw.println("補助座標," + s.determineAX() + "," + s.determineAY() + "," + s.determineBX() + "," + s.determineBY());
            }

            Camera camera = save.getCreasePatternCamera() != null ? save.getCreasePatternCamera() : new Camera();
            pw.println("<camera_of_orisen_nyuuryokuzu>");
            pw.println("<camera_ichi_x>" + camera.getCameraPositionX() + "</camera_ichi_x>");
            pw.println("<camera_ichi_y>" + camera.getCameraPositionY() + "</camera_ichi_y>");
            pw.println("<camera_kakudo>" + camera.getCameraAngle() + "</camera_kakudo>");
            pw.println("<camera_kagami>" + camera.getCameraMirror() + "</camera_kagami>");
            pw.println("<camera_bairitsu_x>" + camera.getCameraZoomX() + "</camera_bairitsu_x>");
            pw.println("<camera_bairitsu_y>" + camera.getCameraZoomY() + "</camera_bairitsu_y>");
            pw.println("<hyouji_ichi_x>" + camera.getDisplayPositionX() + "</hyouji_ichi_x>");
            pw.println("<hyouji_ichi_y>" + camera.getDisplayPositionY() + "</hyouji_ichi_y>");
            pw.println("</camera_of_orisen_nyuuryokuzu>");

            pw.println("<settei>");
            ApplicationModel applicationModel = save.getApplicationModel() != null ? save.getApplicationModel() : new ApplicationModel();
            pw.println("<ckbox_mouse_settei>" + applicationModel.getMouseWheelMovesCreasePattern() + "</ckbox_mouse_settei>");
            pw.println("<ckbox_ten_sagasi>" + applicationModel.getDisplayPointSpotlight() + "</ckbox_ten_sagasi>");
            pw.println("<ckbox_ten_hanasi>" + applicationModel.getDisplayPointOffset() + "</ckbox_ten_hanasi>");
            pw.println("<ckbox_kou_mitudo_nyuuryoku>" + applicationModel.getDisplayGridInputAssist() + "</ckbox_kou_mitudo_nyuuryoku>");
            pw.println("<ckbox_bun>" + applicationModel.getDisplayComments() + "</ckbox_bun>");
            pw.println("<ckbox_cp>" + applicationModel.getDisplayCpLines() + "</ckbox_cp>");
            pw.println("<ckbox_a0>" + applicationModel.getDisplayAuxLines() + "</ckbox_a0>");
            pw.println("<ckbox_a1>" + applicationModel.getDisplayLiveAuxLines() + "</ckbox_a1>");
            pw.println("<ckbox_mejirusi>" + applicationModel.getDisplayMarkings() + "</ckbox_mejirusi>");
            pw.println("<ckbox_cp_ue>" + applicationModel.getDisplayCreasePatternOnTop() + "</ckbox_cp_ue>");
            pw.println("<ckbox_oritatami_keika>" + applicationModel.getDisplayFoldingProgress() + "</ckbox_oritatami_keika>");
            //The thickness of the line in the development view.
            pw.println("<iTenkaizuSenhaba>" + applicationModel.getLineWidth() + "</iTenkaizuSenhaba>");
            //Width of vertex sign
            pw.println("<ir_ten>" + applicationModel.getPointSize() + "</ir_ten>");
            //Express the polygonal line expression with color
            pw.println("<i_orisen_hyougen>" + applicationModel.getLineStyle() + "</i_orisen_hyougen>");
            pw.println("<i_anti_alias>" + applicationModel.getAntiAlias() + "</i_anti_alias>");
            pw.println("</settei>");

            GridModel gridModel = save.getGridModel() != null ? save.getGridModel() : new GridModel();
            pw.println("<Kousi>");
            pw.println("<i_kitei_jyoutai>" + gridModel.getBaseState() + "</i_kitei_jyoutai>");
            pw.println("<nyuuryoku_kitei>" + gridModel.getGridSize() + "</nyuuryoku_kitei>");

            pw.println("<memori_kankaku>" + gridModel.getIntervalGridSize() + "</memori_kankaku>");
            pw.println("<a_to_heikouna_memori_iti>" + gridModel.getHorizontalScalePosition() + "</a_to_heikouna_memori_iti>");
            pw.println("<b_to_heikouna_memori_iti>" + gridModel.getVerticalScalePosition() + "</b_to_heikouna_memori_iti>");
            pw.println("<kousi_senhaba>" + applicationModel.getGridLineWidth() + "</kousi_senhaba>");

            pw.println("<d_kousi_x_a>" + gridModel.getGridXA() + "</d_kousi_x_a>");
            pw.println("<d_kousi_x_b>" + gridModel.getGridXB() + "</d_kousi_x_b>");
            pw.println("<d_kousi_x_c>" + gridModel.getGridXC() + "</d_kousi_x_c>");
            pw.println("<d_kousi_y_a>" + gridModel.getGridYA() + "</d_kousi_y_a>");
            pw.println("<d_kousi_y_b>" + gridModel.getGridYB() + "</d_kousi_y_b>");
            pw.println("<d_kousi_y_c>" + gridModel.getGridYC() + "</d_kousi_y_c>");
            pw.println("<d_kousi_kakudo>" + gridModel.getGridAngle() + "</d_kousi_kakudo>");
            pw.println("</Kousi>");

            pw.println("<Kousi_iro>");
            pw.println("<kousi_color_R>" + applicationModel.getGridColor().getRed() + "</kousi_color_R>");
            pw.println("<kousi_color_G>" + applicationModel.getGridColor().getGreen() + "</kousi_color_G>");
            pw.println("<kousi_color_B>" + applicationModel.getGridColor().getBlue() + "</kousi_color_B>");

            pw.println("<kousi_memori_color_R>" + applicationModel.getGridScaleColor().getRed() + "</kousi_memori_color_R>");
            pw.println("<kousi_memori_color_G>" + applicationModel.getGridScaleColor().getGreen() + "</kousi_memori_color_G>");
            pw.println("<kousi_memori_color_B>" + applicationModel.getGridScaleColor().getBlue() + "</kousi_memori_color_B>");
            pw.println("</Kousi_iro>");

            pw.println("<oriagarizu>");

            FoldedFigureModel foldedFigureModel = save.getFoldedFigureModel() != null ? save.getFoldedFigureModel() : new FoldedFigureModel();
            pw.println("<oriagarizu_F_color_R>" + foldedFigureModel.getFrontColor().getRed() + "</oriagarizu_F_color_R>");
            pw.println("<oriagarizu_F_color_G>" + foldedFigureModel.getFrontColor().getGreen() + "</oriagarizu_F_color_G>");
            pw.println("<oriagarizu_F_color_B>" + foldedFigureModel.getFrontColor().getBlue() + "</oriagarizu_F_color_B>");

            pw.println("<oriagarizu_B_color_R>" + foldedFigureModel.getBackColor().getRed() + "</oriagarizu_B_color_R>");
            pw.println("<oriagarizu_B_color_G>" + foldedFigureModel.getBackColor().getGreen() + "</oriagarizu_B_color_G>");
            pw.println("<oriagarizu_B_color_B>" + foldedFigureModel.getBackColor().getBlue() + "</oriagarizu_B_color_B>");

            pw.println("<oriagarizu_L_color_R>" + foldedFigureModel.getLineColor().getRed() + "</oriagarizu_L_color_R>");
            pw.println("<oriagarizu_L_color_G>" + foldedFigureModel.getLineColor().getGreen() + "</oriagarizu_L_color_G>");
            pw.println("<oriagarizu_L_color_B>" + foldedFigureModel.getLineColor().getBlue() + "</oriagarizu_L_color_B>");

            pw.println("</oriagarizu>");
        } catch (IOException e) {
            Logger.error(e, "Error during Orh export");
        }
    }

    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".orh");
    }

    @Override
    public String getName() {
        return "Orihime save";
    }

    @Override
    public String getExtension() {
        return ".orh";
    }
}
