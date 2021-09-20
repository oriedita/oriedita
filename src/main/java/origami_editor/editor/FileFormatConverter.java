package origami_editor.editor;

import origami.crease_pattern.element.*;
import origami.crease_pattern.element.Point;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.editor.databinding.GridModel;
import origami_editor.graphic2d.grid.Grid;
import origami_editor.record.Memo;
import origami_editor.tools.Camera;
import origami_editor.tools.StringOp;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFormatConverter {

    static Memo orihime2svg(Memo mem_tenkaizu, Memo mem_oriagarizu) {
        System.out.println("svg画像出力");
        Memo MemR = new Memo();

        MemR.reset();

        MemR.addLine("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");

        MemR.addMemo(mem_tenkaizu);
        MemR.addMemo(mem_oriagarizu);

        MemR.addLine("</svg>");
        return MemR;
    }
}
