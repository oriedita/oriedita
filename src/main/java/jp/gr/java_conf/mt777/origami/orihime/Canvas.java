package jp.gr.java_conf.mt777.origami.orihime;

import jp.gr.java_conf.mt777.graphic2d.linesegment.LineSegment;
import jp.gr.java_conf.mt777.graphic2d.point.Point;
import jp.gr.java_conf.mt777.origami.orihime.egaki_syokunin.Drawing_Worker;
import jp.gr.java_conf.mt777.origami.orihime.oriagari_zu.FoldedFigure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private final Drawing_Worker es1;
    private App orihime_app;

    Graphics bufferGraphics;
    BufferedImage offscreen = null;//20181205new

    public Canvas(App app0) {
        orihime_app = app0;

        offscreen = new BufferedImage(2000, 1100, BufferedImage.TYPE_INT_BGR);
        bufferGraphics = offscreen.createGraphics();    //20170107_new

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        
        es1 = app0.es1;
    }

    @Override
    public void paintComponent(Graphics g) {
        //「f」を付けることでfloat型の数値として記述することができる
        Graphics2D g2 = (Graphics2D) bufferGraphics;
        //Graphics2D g2d = (Graphics2D)g;
        //BasicStroke BStroke = new BasicStroke(1.0f);g2.setStroke(BStroke);//線の太さ

        //float fLineWidth=(float)iLineWidth;	float f_h_lineWidth=(float)i_h_lineWidth;
        orihime_app.fLineWidth = (float) orihime_app.iLineWidth;
        orihime_app.f_h_lineWidth = (float) orihime_app.i_h_lineWidth;

        if (orihime_app.antiAlias) {
            orihime_app.fLineWidth = orihime_app.fLineWidth + 0.2f;
            orihime_app.f_h_lineWidth = orihime_app.f_h_lineWidth + 0.2f;
        }

        BasicStroke BStroke = new BasicStroke(orihime_app.fLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        g2.setStroke(BStroke);//線の太さや線の末端の形状

        //BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);g2.setStroke(BStroke);//線の太さや線の末端の形状
        //アンチエイリアス　オフ
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, orihime_app.antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);//アンチエイリアス　オン

        g2.setBackground(Color.WHITE);    //この行は、画像をファイルに書き出そうとしてBufferedImageクラスを使う場合、デフォルトで背景が黒になるので、それを避けるための意味　20170107
        //画像をファイルに書き出さすことはやめて、、BufferedImageクラスを使わず、Imageクラスだけですむなら不要の行

        //別の重なりさがし　のボタンの色の指定。
        if (orihime_app.OZ.findAnotherOverlapValid) {
            orihime_app.Button3.setBackground(new Color(200, 200, 200));//これがないとForegroundが直ぐに反映されない。仕様なのか？
            orihime_app.Button3.setForeground(Color.black);

            orihime_app.Button_AS_matome.setBackground(new Color(200, 200, 200));//これがないとForegroundが直ぐに反映されない。仕様なのか？
            orihime_app.Button_AS_matome.setForeground(Color.black);

            orihime_app.Button_bangou_sitei_suitei_display.setBackground(new Color(200, 200, 200));//これがないとForegroundが直ぐに反映されない。仕様なのか？
            orihime_app.Button_bangou_sitei_suitei_display.setForeground(Color.black);
        } else {
            orihime_app.Button3.setBackground(new Color(201, 201, 201));
            orihime_app.Button3.setForeground(Color.gray);

            orihime_app.Button_AS_matome.setBackground(new Color(201, 201, 201));
            orihime_app.Button_AS_matome.setForeground(Color.gray);

            orihime_app.Button_bangou_sitei_suitei_display.setBackground(new Color(201, 201, 201));
            orihime_app.Button_bangou_sitei_suitei_display.setForeground(Color.gray);
        }

        // バッファー画面のクリア
        orihime_app.dim = getSize();
        bufferGraphics.clearRect(0, 0, orihime_app.dim.width, orihime_app.dim.height);

        //System.out.println("画面サイズ=(" + dim.width + " , " + dim.height  + ")"  );


        //int   i_ten_sagasi_hyouji, i_ten_hanasi_hyouji,i_kou_mitudo_nyuuryoku_hyouji,i_bun_hyouji,i_cp_hyouji,i_a0_hyouji,i_a1_hyouji;
        //int   i_mejirusi_hyouji,i_cp_ue_hyouji,i_oritatami_keika_hyouji;

        orihime_app.i_point_sagasi_display = orihime_app.ckbox_point_search.isSelected();
        orihime_app.i_point_hanasi_display = orihime_app.ckbox_ten_hanasi.isSelected();
        orihime_app.i_kou_mitudo_nyuuryoku_display = orihime_app.ckbox_kou_mitudo_nyuuryoku.isSelected();
        orihime_app.i_bun_display = orihime_app.ckbox_bun.isSelected();
        orihime_app.i_cp_display = orihime_app.ckbox_cp.isSelected();
        orihime_app.i_a0_display = orihime_app.ckbox_a0.isSelected();
        orihime_app.i_a1_display = orihime_app.ckbox_a1.isSelected();

        orihime_app.i_mark_display = orihime_app.ckbox_mark.isSelected();
        orihime_app.i_cp_ue_display = orihime_app.ckbox_cp_ue.isSelected();
        orihime_app.i_oritatami_keika_display = orihime_app.ckbox_oritatami_keika.isSelected();


        bufferGraphics.setColor(Color.red);
        //描画したい内容は以下に書くことVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV


        //カメラのセット

        orihime_app.es1.setCamera(orihime_app.camera_of_orisen_input_diagram);

        FoldedFigure OZi;
        for (int i = 1; i <= orihime_app.OAZ.size() - 1; i++) {
            OZi = orihime_app.OAZ.get(i);
            OZi.cp_worker1.setCamera(orihime_app.camera_of_orisen_input_diagram);
        }

//VVVVVVVVVVVVVVV以下のts2へのカメラセットはOriagari_zuのoekakiで実施しているので以下の5行はなくてもいいはず　20180225
        orihime_app.OZ.cp_worker2.setCamera(orihime_app.OZ.camera_of_foldedFigure);
        orihime_app.OZ.cp_worker2.setCam_front(orihime_app.OZ.camera_of_foldedFigure_front);
        orihime_app.OZ.cp_worker2.setCam_rear(orihime_app.OZ.camera_of_foldedFigure_rear);
        orihime_app.OZ.cp_worker2.setCam_transparent_front(orihime_app.OZ.camera_of_transparent_front);
        orihime_app.OZ.cp_worker2.setCam_transparent_rear(orihime_app.OZ.camera_of_transparent_rear);
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

        //System.out.println("paint　+++++++++++++++++++++　背景表示");
        //背景表示
        if ((orihime_app.img_background != null) && (orihime_app.iDisplayBackground >= 1)) {
            int iw = orihime_app.img_background.getWidth(this);//イメージの幅を取得
            int ih = orihime_app.img_background.getHeight(this);//イメージの高さを取得

            //System.out.println("paint幅＝"+iw);
            //System.out.println("paint高さ＝"+ih);
            orihime_app.h_cam.setBackgroundWidth(iw);
            orihime_app.h_cam.setBackgroundHeight(ih);

            //if(i_Lock_on==1){
            drawBackground(g2, orihime_app.img_background);
            //}
        }

        //格子表示
        //es1.kousi_oekaki_with_camera(bufferGraphics,i_bun_hyouji,i_cp_hyouji,i_a0_hyouji,i_a1_hyouji,fLineWidth,lineStyle,f_h_lineWidth,dim.width,dim.height);//渡す情報はカメラ設定、線幅、画面X幅、画面y高さ


        //解説表示
        //System.out.println("paint　+++++++++++++++++++++　解説表示  " +iDisplayExplanation );
        if ((orihime_app.img_explanation != null) && (orihime_app.iDisplayExplanation >= 1)) {
            bufferGraphics.drawImage(orihime_app.img_explanation, 650, 100, this);//80,80,は描画開始位置

            //bufferGraphics.drawImage(img_explanation,600,150,this);//80,80,は描画開始位置
            //	System.out.println("paint幅＝"+img_background.getWidth(this));
            //	System.out.println("paint高さ＝"+img_background.getHeight(this));
        }


        //基準面の表示
        //System.out.println("paint　+++++++++++++++++++++　基準面の表示");
        if (orihime_app.i_mark_display) {
            if (orihime_app.OZ.displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                //	ts1.setCamera(camera_of_orisen_nyuuryokuzu);
                orihime_app.OZ.cp_worker1.drawing_referencePlane_with_camera(bufferGraphics);//ts1が折り畳みを行う際の基準面を表示するのに使う。
            }
        }

        double d_haba = orihime_app.camera_of_orisen_input_diagram.getCameraZoomX() * orihime_app.es1.get_d_decision_width();
        //Flashlight (dot) search range
        if (orihime_app.i_point_sagasi_display) {
            g2.setColor(new Color(255, 240, 0, 30));
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(new Color(255, 240, 0, 230));
            g2.draw(new Ellipse2D.Double(orihime_app.p_mouse_TV_iti.getX() - d_haba, orihime_app.p_mouse_TV_iti.getY() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
        }

        //Luminous flux of flashlight, etc.
        if (orihime_app.i_point_sagasi_display && orihime_app.i_point_hanasi_display) {
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(new Color(255, 240, 0, 170));
        }


        //展開図表示
        //System.out.println("paint　+++++++++++++++++++++　展開図表示(展開図動かし中心の十字を含む)");
        //if (iDisplayBackground<=1) {
        //        es1.setCamera(camera_of_orisen_nyuuryokuzu);

        orihime_app.es1.draw_with_camera(bufferGraphics, orihime_app.i_bun_display, orihime_app.i_cp_display, orihime_app.i_a0_display, orihime_app.i_a1_display, orihime_app.fLineWidth, orihime_app.lineStyle, orihime_app.f_h_lineWidth, orihime_app.dim.width, orihime_app.dim.height, orihime_app.i_mark_display);//渡す情報はカメラ設定、線幅、画面X幅、画面y高さ,展開図動かし中心の十字の目印の表示

        if (orihime_app.i_bun_display) {
            //展開図情報の文字表示
            bufferGraphics.setColor(Color.black);

            bufferGraphics.drawString("mouse= (   " + orihime_app.p_mouse_object_iti.getX() + "   ,   " + orihime_app.p_mouse_object_iti.getY() + "   )", 120, 75); //この表示内容はvoid kekka_syoriで決められる。

            bufferGraphics.drawString("L=" + orihime_app.es1.getTotal(), 120, 90); //この表示内容はvoid kekka_syoriで決められる。

            //System.out.println("paint　+++++++++++++++++++++　結果の文字表示");
            //結果の文字表示
            bufferGraphics.drawString(orihime_app.OZ.text_result, 120, 105); //この表示内容はvoid kekka_syoriで決められる。

            if (orihime_app.i_kou_mitudo_nyuuryoku_display) {
                jp.gr.java_conf.mt777.graphic2d.point.Point kus_sisuu = new Point(orihime_app.es1.get_moyori_ten_sisuu(orihime_app.p_mouse_TV_iti));//20201024高密度入力がオンならばrepaint（画面更新）のたびにここで最寄り点を求めているので、描き職人で別途最寄り点を求めていることと二度手間になっている。

                double dx_ind = kus_sisuu.getX();
                double dy_ind = kus_sisuu.getY();
                int ix_ind = (int) Math.round(dx_ind);
                int iy_ind = (int) Math.round(dy_ind);
                bufferGraphics.drawString("(" + ix_ind + "," + iy_ind + ")", (int) orihime_app.p_mouse_TV_iti.getX() + 25, (int) orihime_app.p_mouse_TV_iti.getY() + 20); //この表示内容はvoid kekka_syoriで決められる。
            }

            if (orihime_app.subThreadRunning) {
                bufferGraphics.setColor(Color.red);

                bufferGraphics.drawString("Under Calculation. If you want to cancel calculation, uncheck [check A + MV]on right side and press the brake button (bicycle brake icon) on lower side.", 120, 134); //この表示内容はvoid kekka_syoriで決められる。
                bufferGraphics.drawString("計算中。　なお、計算を取り消し通常状態に戻りたいなら、右辺の[check A+MV]のチェックをはずし、ブレーキボタン（下辺の、自転車のブレーキのアイコン）を押す。 ", 120, 148); //この表示内容はvoid kekka_syoriで決められる。
            }

            orihime_app.bulletinBoard.draw(bufferGraphics);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        }


        //折り上がりの各種お絵かき
        //Oriagari_Zu OZi;
        for (int i = 1; i <= orihime_app.OAZ.size() - 1; i++) {
            OZi = orihime_app.OAZ.get(i);
            OZi.foldUp_draw(bufferGraphics, orihime_app.i_mark_display);
        }
        //OZ = (Oriagari_Zu)OAZ.get(OAZ.size()-1);//折りあがり図

        //展開図を折り上がり図の上に描くために、展開図を再表示する
        if (orihime_app.i_cp_ue_display) {
            orihime_app.es1.draw_with_camera(bufferGraphics, orihime_app.i_bun_display, orihime_app.i_cp_display, orihime_app.i_a0_display, orihime_app.i_a1_display, orihime_app.fLineWidth, orihime_app.lineStyle, orihime_app.f_h_lineWidth, orihime_app.dim.width, orihime_app.dim.height, orihime_app.i_mark_display);//渡す情報はカメラ設定、線幅、画面X幅、画面y高さ
        }

        //アンチェイリアス
        //アンチェイリアス　オフ
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, orihime_app.antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オン

        //	bufferGraphics.drawString(c.valueOf(oc.kakudo(new Ten(0.0,0.0),new Ten( 10.0, 0.0))), 30,170);
        //      bufferGraphics.drawString(c.valueOf(778),150,150);
        //test_oekaki();
        //System.out.println("paint　+++++++++++++++++++++　bufferGraphicsへの描画終了");

        //中央指示線
        if (orihime_app.i_point_hanasi_display) {
            g2.setStroke(new BasicStroke(1.0f));
            g2.setColor(Color.black);
            g2.drawLine((int) (orihime_app.p_mouse_TV_iti.getX()), (int) (orihime_app.p_mouse_TV_iti.getY()),
                    (int) (orihime_app.p_mouse_TV_iti.getX() + d_haba), (int) (orihime_app.p_mouse_TV_iti.getY() + d_haba)); //直線
        }


        //描画したい内容はここまでAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA


        // オフスクリーンイメージを実際に描画する。オフスクリーンの幅は最初は 0,0。
        g.drawImage(offscreen, 0, 0, this);

        if (orihime_app.OZ.summary_write_image_during_execution) {//Meaning during summary writing)
            orihime_app.writeImageFile(orihime_app.fname_and_number);

            orihime_app.w_image_running = false;
        }

        if (orihime_app.flg_wi) {//For control when exporting with a frame 20180525
            orihime_app.flg_wi = false;
            orihime_app.writeImageFile(orihime_app.fname_wi);
        }
        if (orihime_app.flg61) {
            orihime_app.flg61 = false;
            orihime_app.es1.setDrawingStage(4);
        }
    }

    public void drawBackground(Graphics2D g2h, Image imgh) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        //背景画を、画像の左上はしを、ウィンドウの(0,0)に合わせて回転や拡大なしで表示した場合を基準状態とする。
        //背景画上の点h1を中心としてa倍拡大する。次に、h1を展開図上の点h3と重なるように背景画を平行移動する。
        //この状態の展開図を、h3を中心にb度回転したよう見えるように座標を回転させて貼り付けて、その後、座標の回転を元に戻すという関数。
        //引数は、Graphics2D g2h,Image imgh,Ten h1,Ten h2,Ten h3,Ten h4
        //h2,とh4も重なるようにする
        //

        //最初に

        //if(i_Lock_on>=10){i_Lock_on=i_Lock_on-10;}
        if (orihime_app.i_Lock_on) {
            orihime_app.h_cam.setCamera(orihime_app.camera_of_orisen_input_diagram);
            orihime_app.h_cam.h3_and_h4_calculation();
            orihime_app.h_cam.parameter_calculation();
        }

        AffineTransform at = new AffineTransform();
        at.rotate(orihime_app.h_cam.getAngle() * Math.PI / 180.0, orihime_app.h_cam.get_cx(), orihime_app.h_cam.get_cy());
        g2h.setTransform(at);


        g2h.drawImage(imgh, orihime_app.h_cam.get_x0(), orihime_app.h_cam.get_y0(), orihime_app.h_cam.get_x1(), orihime_app.h_cam.get_y1(), this);

        //g2h.drawImage(imgh,kaisi_x,kaisi_y,this);//hx0,hy0,は描画開始位置

        at.rotate(-orihime_app.h_cam.getAngle() * Math.PI / 180.0, orihime_app.h_cam.get_cx(), orihime_app.h_cam.get_cy());
        g2h.setTransform(at);

    }

    // マウス操作(マウスが動いた時)を行う関数----------------------------------------------------
    public void mouseMoved(MouseEvent e) {
        //何もしない
        //  final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();//これは多分J2SE 5.0「Tiger」以降で作動するコード

        Point p = new Point(orihime_app.e2p(e));
        orihime_app.mouse_object_iti(p);
        if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_0) {
        } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_FREE_1) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_01(p);
        }   //1 線分入力モード（フリー）
        //else if(orihime_app.i_mouse_modeA==2)  {		}						       //2 展開図移動。
        //else if(orihime_app.i_mouse_modeA==3)  { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_03(p);}//線分削除モード。
        //else if(orihime_app.i_mouse_modeA==4)  { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_04(p);}//senbun_henkan 黒赤青
        else if (orihime_app.i_mouse_modeA == MouseMode.LENGTHEN_CREASE_5) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_05(p);
        }//線分延長モード。
        //else if(orihime_app.i_mouse_modeA==6)  { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_06(p);}//2点から等距離線分モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.SQUARE_BISECTOR_7) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_07(p);
        }//角二等分線モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.INWARD_8) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_08(p);
        }//内心モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.PERPENDICULAR_DRAW_9) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_09(p);
        }//垂線おろしモード。
        else if (orihime_app.i_mouse_modeA == MouseMode.SYMMETRIC_DRAW_10) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_10(p);
        }//折り返しモード。
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_RESTRICTED_11) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_11(p);
        }//線分入力モード。(制限)
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_SYMMETRIC_12) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_12(p);
        }//鏡映モード。
        //else if(orihime_app.i_mouse_modeA==13) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_13(p);}//角度系モード（１番目）。//線分指定、交点まで
        //else if(orihime_app.i_mouse_modeA==14) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_14(p);}//点追加モード。
        //else if(orihime_app.i_mouse_modeA==15) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_15(p);}//点削除モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.ANGLE_SYSTEM_16) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_16(p);
        }//角度系モード（４番目）。2点指定し、線分まで
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_17(p);
        }//角度系モード（２番目）。//2点指定、交点まで
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_18(p);
        }//角度系モード（５番目）。2点指定、自由末端
        //else if(orihime_app.i_mouse_modeA==19) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_19(p);}//select　に使う
        //else if(orihime_app.i_mouse_modeA==20) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_20(p);}//unselect　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MOVE_21) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_21(p);
        }//move　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_COPY_22) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_22(p);
        }//copy_paste　に使う
        //else if(orihime_app.i_mouse_modeA==23) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_23(p);}//--->M　に使う
        //else if(orihime_app.i_mouse_modeA==24) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_24(p);}//--->V　に使う
        //else if(orihime_app.i_mouse_modeA==25) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_25(p);}//--->E　に使う
        //else if(orihime_app.i_mouse_modeA==26) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_26(p);}//背景セット　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.LINE_SEGMENT_DIVISION_27) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_27(p);
        }//線分分割入力　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.LINE_SEGMENT_RATIO_SET_28) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_28(p);
        }//線分内分入力　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.POLYGON_SET_NO_CORNERS_29) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_29(p);
        }//正多角形入力　に使う
        //else if(orihime_app.i_mouse_modeA==30) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_30(p);}//除け_線変換　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MOVE_4P_31) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_31(p);
        }//move 2p2p　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_COPY_4P_32) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_32(p);
        }//copy 2p2p　　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.FISH_BONE_DRAW_33) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_33(p);
        }//魚の骨　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_MV_34) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_34(p);
        }//準備としてだけ使う線分に重複している折線を順に山谷にするの　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DOUBLE_SYMMETRIC_DRAW_35) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_35(p);
        }//複折り返し　入力した線分に接触している折線を折り返し　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASES_ALTERNATE_MV_36) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_36(p);
        }//準備としてだけ使う線分にX交差している折線を順に山谷にするの　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_37(p);
        }//角度系モード（３番目）。角度規格化線分入力モード。角度規格化折線入力　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_38(p);
        }//折り畳み可能線追加
        else if (orihime_app.i_mouse_modeA == MouseMode.FOLDABLE_LINE_INPUT_39) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_39(p);
        }//折り畳み可能線+格子点系入力
        else if (orihime_app.i_mouse_modeA == MouseMode.PARALLEL_DRAW_40) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_40(p);
        }//平行線入力
        //else if(orihime_app.i_mouse_modeA==41) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_41(p);}//点削除（線カラーチェンジ）　に使う
        //else if(orihime_app.i_mouse_modeA==42) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_42(p);}//円入力　に使う
        //else if(orihime_app.i_mouse_modeA==43) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_43(p);}//円の3点入力　に使う
        //else if(orihime_app.i_mouse_modeA==44) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_44(p);}//円　分離入力　に使う
        //else if(orihime_app.i_mouse_modeA==45) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_45(p);}//2円の接線　に使う
        //else if(orihime_app.i_mouse_modeA==46) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_46(p);}//反転　に使う
        //else if(orihime_app.i_mouse_modeA==47) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_47(p);}//円入力モード。(フリー)
        //else if(orihime_app.i_mouse_modeA==48) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_48(p);}//円　同心円追加モード。(元円の円周と同心円の円周との幅は線分で指定する)
        //else if(orihime_app.i_mouse_modeA==49) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_49(p);}//円　同心円追加モード。(元円の円周と同心円の円周との幅は他の同心円の組で指定する)
        //else if(orihime_app.i_mouse_modeA==50) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_50(p);}//2円を指定し、それぞれの円に同心円を加える。それぞれの同心円の組にできる帯領域の幅が等しくなるようにして、加えられた同心円同士が接するようにする。
        //else if(orihime_app.i_mouse_modeA==51) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_51(p);}//平行線　幅指定入力モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_52(p);
        }//連続折り返しモード　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_53(p);
        }//長さ測定１　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_54(p);
        }//長さ測定２　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_55(p);
        }//角度測定１　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_56(p);
        }//角度測定２　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_57(p);
        }//角度測定３　に使う
        //else if(orihime_app.i_mouse_modeA==58) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_58(p);}//senbun_henkan 赤青
        //else if(orihime_app.i_mouse_modeA==59) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_59(p);}//特注プロパティ指定
        //else if(orihime_app.i_mouse_modeA==60) { es1.setCamera(camera_of_orisen_nyuuryokuzu);es1.mMoved_A_60(p);}//--->HK　に使う//HKとは補助活線のこと

        else if (orihime_app.i_mouse_modeA == MouseMode.OPERATION_FRAME_CREATE_61) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_61(p);
        }//長方形内選択（paintの選択に似せた選択機能）に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.VONOROI_CREATE_62) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_62(p);
        }//ボロノイ図　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.FLAT_FOLDABLE_CHECK_63) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_63(p);
        }//外周部折り畳みチェックに使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_DELETE_OVERLAPPING_64) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_64(p);
        }//線内削除　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_DELETE_INTERSECTING_65) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_65(p);
        }//lX　直線で折線削除に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.SELECT_POLYGON_66) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_66(p);
        }//選択＿多角形　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.UNSELECT_POLYGON_67) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_67(p);
        }//非選択＿多角形　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.SELECT_LINE_INTERSECTING_68) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_68(p);
        }//選択＿ｌX　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.UNSELECT_LINE_INTERSECTING_69) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_69(p);
        }//非選択＿ｌX　　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_LENGTHEN_70) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_70(p);
        }//線分延長モード(延長する元の折線のクリックだけで実行されるタイプ)　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.FOLDABLE_LINE_DRAW_71) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mMoved_A_71(p);
        }//複数の線分延長モード　に使う
        else {
        }

        repaint();
    }

    //マウス操作(ボタンを押したとき)を行う関数----------------------------------------------------
    public void mousePressed(MouseEvent e) {

        //Ten p =new Ten(e.getX(),e.getY());
        Point p = new Point(orihime_app.e2p(e));


//wwwwwwwwwwww
        //PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        //MouseInfo.getPointerInfo();

        orihime_app.i_mouseDragged_valid = true;
        orihime_app.i_mouseReleased_valid = true;


        //if (ckbox_mouse_settei.isSelected()){   //20201010　コメントアウト
        orihime_app.btn = e.getButton();
        orihime_app.i_ClickCount = e.getClickCount();

        //---------ボタンの種類による動作変更-----------------------------------------


        if (orihime_app.btn == MouseEvent.BUTTON1) {
            int cnt = e.getClickCount();
            if (cnt == 3) {
                System.out.println("3_Click");//("トリプルクリック"
                if (orihime_app.i_mouse_modeA == MouseMode.CREASE_SELECT_19) {
                    if (orihime_app.ckbox_add_frame_SelectAnd3click_isSelected) {
                        orihime_app.i_mouse_modeA = switch (orihime_app.i_sel_mou_mode) {
                            case MOVE_1 -> MouseMode.CREASE_MOVE_21;
                            case MOVE4P_2 -> MouseMode.CREASE_MOVE_4P_31;
                            case COPY_3 -> MouseMode.CREASE_COPY_22;
                            case COPY4P_4 -> MouseMode.CREASE_COPY_4P_32;
                            case MIRROR_5 -> MouseMode.DRAW_CREASE_SYMMETRIC_12;
                        };

                        System.out.println("i_mouse_modeA=" + orihime_app.i_mouse_modeA);
                    }
                }
            }
        } else if (orihime_app.btn == MouseEvent.BUTTON2) {
            System.out.println("中ボタンクリック");

            orihime_app.i_cp_or_oriagari_decide(p);

            System.out.println("i_cp_or_oriagari = " + orihime_app.i_cp_or_oriagari);

            if (orihime_app.i_cp_or_oriagari == 0) {// 展開図移動。
                orihime_app.camera_of_orisen_input_diagram.camera_ichi_sitei_from_TV(p);
            } else if (orihime_app.i_cp_or_oriagari == 1) {
                orihime_app.OZ.camera_of_foldedFigure_front.camera_ichi_sitei_from_TV(p);
            } else if (orihime_app.i_cp_or_oriagari == 2) {
                orihime_app.OZ.camera_of_foldedFigure_rear.camera_ichi_sitei_from_TV(p);
            } else if (orihime_app.i_cp_or_oriagari == 3) {
                orihime_app.OZ.camera_of_transparent_front.camera_ichi_sitei_from_TV(p);
            } else if (orihime_app.i_cp_or_oriagari == 4) {
                orihime_app.OZ.camera_of_transparent_rear.camera_ichi_sitei_from_TV(p);
            }

            orihime_app.mouse_temp0.set(p);
            repaint();
            return;

        } else if (orihime_app.btn == MouseEvent.BUTTON3) {//右ボタンクリック
            if (orihime_app.i_mouse_modeA == MouseMode.VONOROI_CREATE_62) {//ボロノイ図入力時は、入力途中のボロノイ母点が消えないように、右クリックに反応させない。20181208
            } else {
                orihime_app.i_mouse_right_button_on = true;

                //線分削除モード。
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mPressed_A_03(p);

                orihime_app.foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.BOTH_4;//= 0 is polygonal line input = 1 is auxiliary line input mode, 4 is for both
                es1.setFoldLineAdditional(orihime_app.foldLineAdditionalInputMode);

            }
            repaint();

            return;
        }
        //-----------------------------System.out.println("a");----------------------

        //}  //20201010　コメントアウト


        if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_0) {
        } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_FREE_1) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_01(p);
        }   //1 線分入力モード（フリー）
        else if (orihime_app.i_mouse_modeA == MouseMode.MOVE_CREASE_PATTERN_2) {                                       //2 展開図移動。
            orihime_app.camera_of_orisen_input_diagram.camera_ichi_sitei_from_TV(p);
            orihime_app.mouse_temp0.set(p);
        } else if (orihime_app.i_mouse_modeA == MouseMode.LINE_SEGMENT_DELETE_3) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_03(p);
        }//線分削除モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.CHANGE_CREASE_TYPE_4) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_04(p);
        }//senbun_henkan 黒赤青
        else if (orihime_app.i_mouse_modeA == MouseMode.LENGTHEN_CREASE_5) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_05(p);
        }//線分延長モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_6) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_06(p);
        }//2点から等距離線分モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.SQUARE_BISECTOR_7) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_07(p);
        }//角二等分線モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.INWARD_8) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_08(p);
        }//内心モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.PERPENDICULAR_DRAW_9) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_09(p);
        }//垂線おろしモード。
        else if (orihime_app.i_mouse_modeA == MouseMode.SYMMETRIC_DRAW_10) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_10(p);
        }//折り返しモード。
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_RESTRICTED_11) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_11(p);
        }//線分入力モード。(制限)
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_SYMMETRIC_12) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_12(p);
        }//鏡映モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_13(p);
        }//角度系モード（１番目）。//線分指定、交点まで
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_POINT_14) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_14(p);
        }//点追加モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.DELETE_POINT_15) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_15(p);
        }//点削除モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.ANGLE_SYSTEM_16) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_16(p);
        }//角度系モード（４番目）。2点指定し、線分まで
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_17(p);
        }//角度系モード（２番目）。//2点指定、交点まで
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_18(p);
        }//角度系モード（５番目）。2点指定、自由末端
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_SELECT_19) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_19(p);
        }//select　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_UNSELECT_20) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_20(p);
        }//unselect　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MOVE_21) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_21(p);
        }//move　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_COPY_22) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_22(p);
        }//copy_paste　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_MOUNTAIN_23) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_23(p);
        }//--->M　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_VALLEY_24) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_24(p);
        }//--->V　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_EDGE_25) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_25(p);
        }//--->E　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.BACKGROUND_CHANGE_POSITION_26) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_26(p);
        }//背景セット　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.LINE_SEGMENT_DIVISION_27) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_27(p);
        }//線分分割入力　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.LINE_SEGMENT_RATIO_SET_28) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_28(p);
        }//線分内分入力　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.POLYGON_SET_NO_CORNERS_29) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_29(p);
        }//正多角形入力　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_ADVANCE_TYPE_30) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_30(p);
        }//除け_線変換　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MOVE_4P_31) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_31(p);
        }//move 2p2p　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_COPY_4P_32) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_32(p);
        }//copy 2p2p　　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.FISH_BONE_DRAW_33) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_33(p);
        }//魚の骨　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_MV_34) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_34(p);
        }//準備としてだけ使う線分に重複している折線を順に山谷にするの　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DOUBLE_SYMMETRIC_DRAW_35) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_35(p);
        }//複折り返し　入力した線分に接触している折線を折り返し　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASES_ALTERNATE_MV_36) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_36(p);
        }//準備としてだけ使う線分にX交差している折線を順に山谷にするの　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_37(p);
        }//角度系モード（３番目）。角度規格化線分入力モード。角度規格化折線入力　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_38(p);
        }//折り畳み可能線追加
        else if (orihime_app.i_mouse_modeA == MouseMode.FOLDABLE_LINE_INPUT_39) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_39(p);
        }//折り畳み可能線+格子点系入力
        else if (orihime_app.i_mouse_modeA == MouseMode.PARALLEL_DRAW_40) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_40(p);
        }//平行線入力
        else if (orihime_app.i_mouse_modeA == MouseMode.VERTEX_DELETE_ON_CREASE_41) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_41(p);
        }//点削除（線カラーチェンジ）　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_42) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_42(p);
        }//円入力　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_THREE_POINT_43) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_43(p);
        }//円の3点入力　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_SEPARATE_44) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_44(p);
        }//円　分離入力　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_TANGENT_LINE_45) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_45(p);
        }//2円の接線　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.INVERTED_INPUT_46) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_46(p);
        }//反転　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_FREE_47) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_47(p);
        }//円入力モード。(フリー)
        else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_CONCENTRIC_48) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_48(p);
        }//円　同心円追加モード。(元円の円周と同心円の円周との幅は線分で指定する)
        else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_49(p);
        }//円　同心円追加モード。(元円の円周と同心円の円周との幅は他の同心円の組で指定する)
        else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_50(p);
        }//2円を指定し、それぞれの円に同心円を加える。それぞれの同心円の組にできる帯領域の幅が等しくなるようにして、加えられた同心円同士が接するようにする。
        else if (orihime_app.i_mouse_modeA == MouseMode.PARALLEL_DRAW_WIDTH_51) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_51(p);
        }//平行線　幅指定入力モード。
        else if (orihime_app.i_mouse_modeA == MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_52(p);
        }//連続折り返しモードに使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_53(p);
        }//長さ測定１　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_54(p);
        }//長さ測定２　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_55(p);
        }//角度測定１　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_56(p);
        }//角度測定２　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_57(p);
        }//角度測定３　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_TOGGLE_MV_58) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_58(p);
        }//senbun_henkan 赤青
        else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_CHANGE_COLOR_59) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_59(p);
        }//特注プロパティ指定
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_AUX_60) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_60(p);
        }//--->HK　に使う//HKとは補助活線のこと

        else if (orihime_app.i_mouse_modeA == MouseMode.OPERATION_FRAME_CREATE_61) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_61(p);
        }//長方形内選択（paintの選択に似せた選択機能）に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.VONOROI_CREATE_62) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_62(p);
        }//ボロノイ図　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.FLAT_FOLDABLE_CHECK_63) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_63(p);
        }//外周部折り畳みチェックに使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_DELETE_OVERLAPPING_64) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_64(p);
        }//線内削除　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_DELETE_INTERSECTING_65) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_65(p);
        }//lX線内削除　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.SELECT_POLYGON_66) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_66(p);
        }//unselect　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.UNSELECT_POLYGON_67) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_67(p);
        }//unselect　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.SELECT_LINE_INTERSECTING_68) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_68(p);
        }//unselect　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.UNSELECT_LINE_INTERSECTING_69) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_69(p);
        }//unselect　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_LENGTHEN_70) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_70(p);
        }//unselect　に使う
        else if (orihime_app.i_mouse_modeA == MouseMode.FOLDABLE_LINE_DRAW_71) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_71(p);
        }//unselect　に使う

        else if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_10001) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_10001(p);
        } else if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_10002) {
            es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
            es1.mPressed_A_10002(p);
        } else if (orihime_app.i_mouse_modeA == MouseMode.MODIFY_CALCULATED_SHAPE_101) {        //折り上がり図操作
            orihime_app.OZ.foldedFigure_operation_mouse_on(p);
        } else if (orihime_app.i_mouse_modeA == MouseMode.MOVE_CALCULATED_SHAPE_102) {//折り上がり図移動
            orihime_app.OZ.camera_of_foldedFigure.camera_ichi_sitei_from_TV(p);
            orihime_app.OZ.camera_of_foldedFigure_front.camera_ichi_sitei_from_TV(p);
            orihime_app.OZ.camera_of_foldedFigure_rear.camera_ichi_sitei_from_TV(p);

            orihime_app.OZ.camera_of_transparent_front.camera_ichi_sitei_from_TV(p);
            orihime_app.OZ.camera_of_transparent_rear.camera_ichi_sitei_from_TV(p);

            orihime_app.mouse_temp0.set(p);
        } else if (orihime_app.i_mouse_modeA == MouseMode.CHANGE_STANDARD_FACE_103) {
            //ts1.set_kijyunmen_id(p);
        }//Reference plane designation

        repaint();

        //add_frame_to_Front();
    }

    //マウス操作(ドラッグしたとき)を行う関数---------- System.out.println("A");------------------------------------------
    public void mouseDragged(MouseEvent e) {

        if (orihime_app.i_mouseDragged_valid) {

            //Ten p =new Ten(e.getX(),e.getY());
            Point p = new Point(orihime_app.e2p(e));
            orihime_app.mouse_object_iti(p);

            //if (ckbox_mouse_settei.isSelected()){  //20201010　コメントアウト
            //---------ボタンの種類による動作変更-----------------------------------------
            if (orihime_app.btn == MouseEvent.BUTTON1) {


            } else if (orihime_app.btn == MouseEvent.BUTTON2) {
                //System.out.println("中ボタンクリック");
                //if(ts2.naibu_hantei(p)==0){
                //i_cp_or_oriagari=0;
                //if(ts2.naibu_hantei_ura(p)>0){i_cp_or_oriagari=2;}
                //if(ts2.naibu_hantei_omote(p)>0){i_cp_or_oriagari=1;}


                if (orihime_app.i_cp_or_oriagari == 0) {// 展開図移動。
                    orihime_app.camera_of_orisen_input_diagram.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                } else if (orihime_app.i_cp_or_oriagari == 1) {
                    orihime_app.OZ.camera_of_foldedFigure_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                } else if (orihime_app.i_cp_or_oriagari == 2) {
                    orihime_app.OZ.camera_of_foldedFigure_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                } else if (orihime_app.i_cp_or_oriagari == 3) {
                    orihime_app.OZ.camera_of_transparent_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                } else if (orihime_app.i_cp_or_oriagari == 4) {
                    orihime_app.OZ.camera_of_transparent_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                }

                orihime_app.mouse_temp0.set(p);
                repaint();
                return;

            } else if (orihime_app.btn == MouseEvent.BUTTON3) {
                //System.out.println("右ボタンクリック");
                if (orihime_app.i_mouse_modeA == MouseMode.VONOROI_CREATE_62) {//ボロノイ図入力時は、入力途中のボロノイ母点が消えないように、右クリックに反応させない。20181208
                } else {
                    if (orihime_app.i_mouse_undo_redo_mode) {
                        return;
                    }//undo,redoモード。
                    es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_03(p);//線分削除モード。
                }
                repaint();
                return;
            }
            //-----------------------------System.out.println("a");----------------------
            //}  //20201010　コメントアウト


            if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_0) {
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_FREE_1) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_01(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.MOVE_CREASE_PATTERN_2) {
                orihime_app.camera_of_orisen_input_diagram.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);


//20180225追加
                FoldedFigure OZi;
                for (int i_oz = 1; i_oz <= orihime_app.OAZ.size() - 1; i_oz++) {
                    OZi = orihime_app.OAZ.get(i_oz);

                    //Ten t_o2tv =new Ten();
                    //t_o2tv =camera_of_orisen_nyuuryokuzu.object2TV(camera_of_orisen_nyuuryokuzu.get_camera_ichi());

//OZi.d_oriagarizu_syukusyaku_keisuu=OZi.d_oriagarizu_syukusyaku_keisuu*d_bairitu;


                    OZi.camera_of_foldedFigure.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    //OZi.camera_of_oriagarizu.kakezan_camera_bairitsu_x(d_bairitu);
                    //OZi.camera_of_oriagarizu.kakezan_camera_bairitsu_y(d_bairitu);

                    OZi.camera_of_foldedFigure_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    //OZi.camera_of_oriagari_omote.kakezan_camera_bairitsu_x(d_bairitu);
                    //OZi.camera_of_oriagari_omote.kakezan_camera_bairitsu_y(d_bairitu);

                    OZi.camera_of_foldedFigure_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    //OZi.camera_of_oriagari_ura.kakezan_camera_bairitsu_x(d_bairitu);
                    //OZi.camera_of_oriagari_ura.kakezan_camera_bairitsu_y(d_bairitu);

                    OZi.camera_of_transparent_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    //OZi.camera_of_touka_omote.kakezan_camera_bairitsu_x(d_bairitu);
                    //OZi.camera_of_touka_omote.kakezan_camera_bairitsu_y(d_bairitu);

                    OZi.camera_of_transparent_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    //OZi.camera_of_touka_ura.kakezan_camera_bairitsu_x(d_bairitu);
                    //OZi.camera_of_touka_ura.kakezan_camera_bairitsu_y(d_bairitu);

                    //text29.setText(String.valueOf(OZ.d_oriagarizu_syukusyaku_keisuu));
                    //text29.setCaretPosition(0);
                }
//20180225追加　ここまで


                orihime_app.mouse_temp0.set(p);


            } else if (orihime_app.i_mouse_modeA == MouseMode.LINE_SEGMENT_DELETE_3) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_03(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CHANGE_CREASE_TYPE_4) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_04(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.LENGTHEN_CREASE_5) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_05(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_6) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_06(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.SQUARE_BISECTOR_7) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_07(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.INWARD_8) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_08(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.PERPENDICULAR_DRAW_9) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_09(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.SYMMETRIC_DRAW_10) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_10(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_RESTRICTED_11) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_11(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_SYMMETRIC_12) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_12(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_13(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_POINT_14) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_14(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DELETE_POINT_15) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_15(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.ANGLE_SYSTEM_16) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_16(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_17(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_18(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_SELECT_19) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_19(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_UNSELECT_20) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_20(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MOVE_21) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_21(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_COPY_22) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_22(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_MOUNTAIN_23) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_23(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_VALLEY_24) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_24(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_EDGE_25) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_25(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.BACKGROUND_CHANGE_POSITION_26) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_26(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.LINE_SEGMENT_DIVISION_27) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_27(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.LINE_SEGMENT_RATIO_SET_28) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_28(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.POLYGON_SET_NO_CORNERS_29) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_29(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_ADVANCE_TYPE_30) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_30(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MOVE_4P_31) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_31(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_COPY_4P_32) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_32(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.FISH_BONE_DRAW_33) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_33(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_MV_34) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_34(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DOUBLE_SYMMETRIC_DRAW_35) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_35(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASES_ALTERNATE_MV_36) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_36(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_37(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_38(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.FOLDABLE_LINE_INPUT_39) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_39(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.PARALLEL_DRAW_40) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_40(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.VERTEX_DELETE_ON_CREASE_41) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_41(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_42) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_42(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_THREE_POINT_43) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_43(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_SEPARATE_44) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_44(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_TANGENT_LINE_45) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_45(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.INVERTED_INPUT_46) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_46(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_FREE_47) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_47(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_CONCENTRIC_48) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_48(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_49(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_50(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.PARALLEL_DRAW_WIDTH_51) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_51(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_52(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_53(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_54(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_55(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_56(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_57(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_TOGGLE_MV_58) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_58(p);
            }//senbun_henkan 赤青
            else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_CHANGE_COLOR_59) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_59(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_AUX_60) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_60(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.OPERATION_FRAME_CREATE_61) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_61(p);
            }//長方形内選択（paintの選択に似せた選択機能）に使う
            else if (orihime_app.i_mouse_modeA == MouseMode.VONOROI_CREATE_62) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_62(p);
            }//ボロノイ図　に使う
            else if (orihime_app.i_mouse_modeA == MouseMode.FLAT_FOLDABLE_CHECK_63) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_63(p);
            }//外周部折り畳みチェックに使う
            else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_DELETE_OVERLAPPING_64) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_64(p);
            }//線内削除　に使う
            else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_DELETE_INTERSECTING_65) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_65(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.SELECT_POLYGON_66) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_66(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.UNSELECT_POLYGON_67) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_67(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.SELECT_LINE_INTERSECTING_68) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_68(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.UNSELECT_LINE_INTERSECTING_69) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_69(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_LENGTHEN_70) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_70(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.FOLDABLE_LINE_DRAW_71) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_71(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_10001) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_10001(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_10002) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mDragged_A_10002(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.MODIFY_CALCULATED_SHAPE_101) {
                orihime_app.OZ.foldedFigure_operation_mouse_drag(p);
            }    //折り上がり図操作
            else if (orihime_app.i_mouse_modeA == MouseMode.MOVE_CALCULATED_SHAPE_102) {
                orihime_app.OZ.camera_of_foldedFigure.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                orihime_app.OZ.camera_of_foldedFigure_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                orihime_app.OZ.camera_of_foldedFigure_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));

                orihime_app.OZ.camera_of_transparent_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                orihime_app.OZ.camera_of_transparent_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));

                orihime_app.mouse_temp0.set(p);//mouse_temp0は一時的に使うTen、mouse_temp0.tano_Ten_iti(p)はmouse_temp0から見たpの位置

            } else if (orihime_app.i_mouse_modeA == MouseMode.CHANGE_STANDARD_FACE_103) {
            }//基準面指定

            repaint();
        }

        //add_frame_to_Front();
    }

    //マウス操作(ボタンをクリックしたとき)を行う関数----------------------------------------------------
    public void mouseClicked(MouseEvent e) {
        //何もしない


    }


    //マウス操作(カーソルが有効領域内に入ったとき)を行う関数----------------------------------------------------
    public void mouseEntered(MouseEvent e) {
        //何もしない
    }

    //マウス操作(カーソルが有効領域外に出たとき)を行う関数----------------------------------------------------
    public void mouseExited(MouseEvent e) {
        //何もしない
    }

    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(MouseEvent e) {
        if (orihime_app.i_mouseReleased_valid) {
            //Ten p =new Ten(e.getX(),e.getY());
            Point p = new Point(orihime_app.e2p(e));


            //if (ckbox_mouse_settei.isSelected()){  //20201010　コメントアウト
            //---------ボタンの種類による動作変更-----------------------------------------
            if (orihime_app.btn == MouseEvent.BUTTON1) {
                //

            } else if (orihime_app.btn == MouseEvent.BUTTON2) {
                //System.out.println("中ボタンクリック");
                //if(ts2.naibu_hantei(p)==0){
                if (orihime_app.i_cp_or_oriagari == 0) {

                    orihime_app.camera_of_orisen_input_diagram.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                } else if (orihime_app.i_cp_or_oriagari == 1) {
                    orihime_app.OZ.camera_of_foldedFigure_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                } else if (orihime_app.i_cp_or_oriagari == 2) {
                    orihime_app.OZ.camera_of_foldedFigure_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));

                } else if (orihime_app.i_cp_or_oriagari == 3) {
                    orihime_app.OZ.camera_of_transparent_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                } else if (orihime_app.i_cp_or_oriagari == 4) {
                    orihime_app.OZ.camera_of_transparent_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                }

                orihime_app.mouse_temp0.set(p);
                repaint();
                orihime_app.i_mouseDragged_valid = false;
                orihime_app.i_mouseReleased_valid = false;
                return;//

            } else if (orihime_app.btn == MouseEvent.BUTTON3) {
                //System.out.println("右ボタンクリック");
                if (orihime_app.i_mouse_modeA == MouseMode.VONOROI_CREATE_62) {
                    repaint();//ボロノイ図入力時は、入力途中のボロノイ母点が消えないように、右クリックに反応させない。20181208
                } else {

                    orihime_app.i_mouse_right_button_on = false;

                    //if(i_mouse_undo_redo_mode==1){i_mouse_undo_redo_mode=0;es1.unselect_all();Button_kyoutuu_sagyou();es1.modosi_i_orisen_hojyosen();return;}
                    if (orihime_app.i_mouse_undo_redo_mode) {
                        orihime_app.i_mouse_undo_redo_mode = false;
                        return;
                    } //undo,redoモード。
                    es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_03(p);
                    repaint();//なんでここにrepaintがあるか検討した方がよいかも。20181208
                    es1.modosi_foldLineAdditional();
                    orihime_app.i_mouseDragged_valid = false;
                    orihime_app.i_mouseReleased_valid = false;
                    //線分削除モード。
                }
                return;
            }
            //----------------------------System.out.println("a");-----------------------
            //}  //20201010　コメントアウト


            if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_0) {
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_FREE_1) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_01(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.MOVE_CREASE_PATTERN_2) {
                orihime_app.camera_of_orisen_input_diagram.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);


//20180225追加
                FoldedFigure OZi;
                for (int i_oz = 1; i_oz <= orihime_app.OAZ.size() - 1; i_oz++) {
                    OZi = orihime_app.OAZ.get(i_oz);

                    //Ten t_o2tv =new Ten();
                    //t_o2tv =camera_of_orisen_nyuuryokuzu.object2TV(camera_of_orisen_nyuuryokuzu.get_camera_ichi());

//OZi.d_oriagarizu_syukusyaku_keisuu=OZi.d_oriagarizu_syukusyaku_keisuu*d_bairitu;


                    OZi.camera_of_foldedFigure.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    //OZi.camera_of_oriagarizu.kakezan_camera_bairitsu_x(d_bairitu);
                    //OZi.camera_of_oriagarizu.kakezan_camera_bairitsu_y(d_bairitu);

                    OZi.camera_of_foldedFigure_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    //OZi.camera_of_oriagari_omote.kakezan_camera_bairitsu_x(d_bairitu);
                    //OZi.camera_of_oriagari_omote.kakezan_camera_bairitsu_y(d_bairitu);

                    OZi.camera_of_foldedFigure_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    //OZi.camera_of_oriagari_ura.kakezan_camera_bairitsu_x(d_bairitu);
                    //OZi.camera_of_oriagari_ura.kakezan_camera_bairitsu_y(d_bairitu);

                    OZi.camera_of_transparent_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    //OZi.camera_of_touka_omote.kakezan_camera_bairitsu_x(d_bairitu);
                    //OZi.camera_of_touka_omote.kakezan_camera_bairitsu_y(d_bairitu);

                    OZi.camera_of_transparent_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                    //OZi.camera_of_touka_ura.kakezan_camera_bairitsu_x(d_bairitu);
                    //OZi.camera_of_touka_ura.kakezan_camera_bairitsu_y(d_bairitu);

                    //text29.setText(String.valueOf(OZ.d_oriagarizu_syukusyaku_keisuu));
                    //text29.setCaretPosition(0);
                }
//20180225追加　ここまで


                orihime_app.mouse_temp0.set(p);


            } else if (orihime_app.i_mouse_modeA == MouseMode.LINE_SEGMENT_DELETE_3) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_03(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CHANGE_CREASE_TYPE_4) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_04(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.LENGTHEN_CREASE_5) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_05(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_6) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_06(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.SQUARE_BISECTOR_7) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_07(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.INWARD_8) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_08(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.PERPENDICULAR_DRAW_9) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_09(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.SYMMETRIC_DRAW_10) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_10(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_RESTRICTED_11) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_11(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_SYMMETRIC_12) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_12(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_13(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_POINT_14) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_14(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DELETE_POINT_15) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_15(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.ANGLE_SYSTEM_16) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_16(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_17(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_18(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_SELECT_19) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_19(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_UNSELECT_20) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_20(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MOVE_21) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_21(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_COPY_22) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_22(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_MOUNTAIN_23) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_23(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_VALLEY_24) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_24(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_EDGE_25) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_25(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.BACKGROUND_CHANGE_POSITION_26) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);

                if (es1.mReleased_A_26(p) == 4) {
                    //i_Lock_on_ori=1;
                    orihime_app.Button_kyoutuu_sagyou();
                    //System.out.println("i_mouse_modeA==4");
                    LineSegment s_1 = new LineSegment();
                    s_1.set(es1.get_s_step(1));
                    LineSegment s_2 = new LineSegment();
                    s_2.set(es1.get_s_step(2));
                    LineSegment s_3 = new LineSegment();
                    s_3.set(es1.get_s_step(3));
                    LineSegment s_4 = new LineSegment();
                    s_4.set(es1.get_s_step(4));

                    //int i_Lock_on_old=i_Lock_on;
                    orihime_app.i_Lock_on = false;
                    orihime_app.Button_background_Lock_on.setBackground(Color.gray);

                    orihime_app.background_set(orihime_app.camera_of_orisen_input_diagram.object2TV(s_1.getA()),
                            orihime_app.camera_of_orisen_input_diagram.object2TV(s_2.getA()),
                            orihime_app.camera_of_orisen_input_diagram.object2TV(s_3.getA()),
                            orihime_app.camera_of_orisen_input_diagram.object2TV(s_4.getA()));

                    //	i_Lock_on=i_Lock_on_old;


                }
            } else if (orihime_app.i_mouse_modeA == MouseMode.LINE_SEGMENT_DIVISION_27) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_27(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.LINE_SEGMENT_RATIO_SET_28) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_28(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.POLYGON_SET_NO_CORNERS_29) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_29(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_ADVANCE_TYPE_30) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_30(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MOVE_4P_31) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_31(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_COPY_4P_32) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_32(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.FISH_BONE_DRAW_33) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_33(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_MV_34) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_34(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DOUBLE_SYMMETRIC_DRAW_35) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_35(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASES_ALTERNATE_MV_36) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_36(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_37(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_38(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.FOLDABLE_LINE_INPUT_39) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_39(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.PARALLEL_DRAW_40) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_40(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.VERTEX_DELETE_ON_CREASE_41) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_41(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_42) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_42(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_THREE_POINT_43) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_43(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_SEPARATE_44) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_44(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_TANGENT_LINE_45) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_45(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.INVERTED_INPUT_46) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_46(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_FREE_47) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_47(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_CONCENTRIC_48) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_48(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_49(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_50(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.PARALLEL_DRAW_WIDTH_51) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_51(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_52(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_53(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_54(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_55(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_56(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_57(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_TOGGLE_MV_58) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_58(p);
            }//senbun_henkan 赤青
            else if (orihime_app.i_mouse_modeA == MouseMode.CIRCLE_CHANGE_COLOR_59) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_59(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_MAKE_AUX_60) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_60(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.OPERATION_FRAME_CREATE_61) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_61(p);
            }//長方形内選択（paintの選択に似せた選択機能）に使う
            else if (orihime_app.i_mouse_modeA == MouseMode.VONOROI_CREATE_62) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_62(p);
            }//ボロノイ図　に使う
            else if (orihime_app.i_mouse_modeA == MouseMode.FLAT_FOLDABLE_CHECK_63) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_63(p);
            }//外周部折り畳みチェックに使う
            else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_DELETE_OVERLAPPING_64) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_64(p);
            }//線内削除　に使う
            else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_DELETE_INTERSECTING_65) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_65(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.SELECT_POLYGON_66) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_66(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.UNSELECT_POLYGON_67) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_67(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.SELECT_LINE_INTERSECTING_68) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_68(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.UNSELECT_LINE_INTERSECTING_69) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_69(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.CREASE_LENGTHEN_70) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_70(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.FOLDABLE_LINE_DRAW_71) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_71(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_10001) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_10001(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.UNUSED_10002) {
                es1.setCamera(orihime_app.camera_of_orisen_input_diagram);
                es1.mReleased_A_10002(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.MODIFY_CALCULATED_SHAPE_101) {        //折り上がり図操作
                orihime_app.OZ.foldedFigure_operation_mouse_off(p);
            } else if (orihime_app.i_mouse_modeA == MouseMode.MOVE_CALCULATED_SHAPE_102) {
                orihime_app.OZ.camera_of_foldedFigure.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                orihime_app.OZ.camera_of_foldedFigure_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                orihime_app.OZ.camera_of_foldedFigure_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));

                orihime_app.OZ.camera_of_transparent_front.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));
                orihime_app.OZ.camera_of_transparent_rear.displayPositionMove(orihime_app.mouse_temp0.other_Point_position(p));

                orihime_app.mouse_temp0.set(p);

            } else if (orihime_app.i_mouse_modeA == MouseMode.CHANGE_STANDARD_FACE_103) {//基準面指定
                int new_referencePlane_id;
                int old_referencePlane_id;
                old_referencePlane_id = orihime_app.OZ.cp_worker1.getReferencePlaneId();

                new_referencePlane_id = orihime_app.OZ.cp_worker1.setReferencePlaneId(p);
                System.out.println("kijyunmen_id = " + new_referencePlane_id);
                if (orihime_app.OZ.ct_worker.face_rating != null) {//20180227追加
                    System.out.println(
                            "OZ.js.nbox.get_jyunjyo = " + orihime_app.OZ.ct_worker.nbox.getSequence(new_referencePlane_id) + " , rating = " +
                                    orihime_app.OZ.ct_worker.nbox.getDouble(orihime_app.OZ.ct_worker.nbox.getSequence(new_referencePlane_id))

                    );

                }
                if ((new_referencePlane_id != old_referencePlane_id) && (orihime_app.OZ.estimationStep != FoldedFigure.EstimationStep.STEP_0)) {
                    orihime_app.OZ.estimationStep = FoldedFigure.EstimationStep.STEP_1;
                }

            }

            repaint();

        }

        orihime_app.i_mouseDragged_valid = false;
        orihime_app.i_mouseReleased_valid = false;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        //System.out.println("mouseWheelMoved   " +e.getWheelRotation());
        if (orihime_app.ckbox_mouse_settings.isSelected()) {
            //	ホイールでundo,redo
            if ((e.isShiftDown()) || (orihime_app.i_mouse_right_button_on)) {
                orihime_app.i_mouse_undo_redo_mode = true;
                es1.unselect_all();
                orihime_app.Button_kyoutuu_sagyou();
                es1.modosi_foldLineAdditional();
                if (e.getWheelRotation() == -1) {
                    orihime_app.setTitle(es1.redo());
                    repaint();
                } else {
                    orihime_app.setTitle(es1.undo());
                    repaint();
                }

            }

            //	ホイールで拡大縮小
            if ((!e.isShiftDown()) && (!orihime_app.i_mouse_right_button_on)) {

                // ---------------------------------------------------------------------hhhhhhhhh

                Point p = new Point(orihime_app.e2p(e));
                orihime_app.i_cp_or_oriagari_decide(p);
                if (orihime_app.i_cp_or_oriagari == 0) {
                    if (e.getWheelRotation() == -1) {
                        orihime_app.d_syukusyaku_keisuu = orihime_app.d_syukusyaku_keisuu * Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
                    } else {
                        orihime_app.d_syukusyaku_keisuu = orihime_app.d_syukusyaku_keisuu / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
                    }
                    orihime_app.camera_of_orisen_input_diagram.setCameraZoomX(orihime_app.d_syukusyaku_keisuu);
                    orihime_app.camera_of_orisen_input_diagram.setCameraZoomY(orihime_app.d_syukusyaku_keisuu);
                    orihime_app.text27.setText(String.valueOf(orihime_app.d_syukusyaku_keisuu));
                    orihime_app.text27.setCaretPosition(0);
                    // ---------------------------------------------------------------------
                } else {
                    if (e.getWheelRotation() == -1) {
                        orihime_app.OZ.d_foldedFigure_scale_factor = orihime_app.OZ.d_foldedFigure_scale_factor * Math.sqrt(Math.sqrt(Math.sqrt(2.0)));
                    } else {
                        orihime_app.OZ.d_foldedFigure_scale_factor = orihime_app.OZ.d_foldedFigure_scale_factor / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));
                    }
                    orihime_app.OZ.camera_of_foldedFigure.setCameraZoomX(orihime_app.OZ.d_foldedFigure_scale_factor);
                    orihime_app.OZ.camera_of_foldedFigure.setCameraZoomY(orihime_app.OZ.d_foldedFigure_scale_factor);
                    orihime_app.OZ.camera_of_foldedFigure_front.setCameraZoomX(orihime_app.OZ.d_foldedFigure_scale_factor);
                    orihime_app.OZ.camera_of_foldedFigure_front.setCameraZoomY(orihime_app.OZ.d_foldedFigure_scale_factor);
                    orihime_app.OZ.camera_of_foldedFigure_rear.setCameraZoomX(orihime_app.OZ.d_foldedFigure_scale_factor);
                    orihime_app.OZ.camera_of_foldedFigure_rear.setCameraZoomY(orihime_app.OZ.d_foldedFigure_scale_factor);
                    orihime_app.OZ.camera_of_transparent_front.setCameraZoomX(orihime_app.OZ.d_foldedFigure_scale_factor);
                    orihime_app.OZ.camera_of_transparent_front.setCameraZoomY(orihime_app.OZ.d_foldedFigure_scale_factor);
                    orihime_app.OZ.camera_of_transparent_rear.setCameraZoomX(orihime_app.OZ.d_foldedFigure_scale_factor);
                    orihime_app.OZ.camera_of_transparent_rear.setCameraZoomY(orihime_app.OZ.d_foldedFigure_scale_factor);
                    orihime_app.text29.setText(String.valueOf(orihime_app.OZ.d_foldedFigure_scale_factor));
                    orihime_app.text29.setCaretPosition(0);
                }
                // ---------------------------------------------------------------------

                orihime_app.mouse_object_iti(orihime_app.p_mouse_TV_iti);
                repaint();
            }
        }
    }
}
