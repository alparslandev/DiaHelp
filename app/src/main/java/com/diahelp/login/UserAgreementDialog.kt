package com.diahelp.login

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Window
import android.widget.TextView
import com.diahelp.R
import kotlinx.android.synthetic.main.dialog_user_agreement.*


class UserAgreementDialog(context: Context) : Dialog(context) {

    init {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_user_agreement)
        getWindow().setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        iv_close.setOnClickListener{ dismiss() }
        val txtBookReferance = findViewById(R.id.txt_referance_book) as TextView
        txtBookReferance.movementMethod = LinkMovementMethod.getInstance()
        txtBookReferance.isClickable = true
        /*if (!isFirstTime) {
                   txtBookReferance.setText(Html.fromHtml("<h4>Hakkında</h4>" +
                           "Bu uygulamada yer alan besin değerleri diyetisyen Emel Özer’in <a style=\"color:white\" href=\"http://www.hayykitap.com/haber.php?haber_id=9\">" +
                           "Diyabetliler İçin Hayatı Kolaylaştırma Kılavuzu</a> adlı kitabından alınmıştır. \n" +
                           "Siz de paketli ürünlerin markasının ve besin değerleri tablosunun fotoğrafını çekip karbonhidratsayar@gmail.com adresine mail göndererek " +
                           "istediğiniz yiyeceklerin veritabanına eklenmesini sağlayabilirsiniz.\n"));

               } else {*/
        txtBookReferance.text = Html.fromHtml(
            "<h4>Kullanıcı Sözleşmesi</h4>Bu uygulama tıbbi bir uygulama değildir. Buradaki bilgiler doktorunuz " +
                    "veya diyetisyeninizin yerine geçmez. Herhangi bir tıbbi tedavi alıyorsanız doktorunuz ve diyetisyeninizin tavsiyelerini esas almanız gerekmektedir. " +
                    "Buradaki bilgiler ve hesaplamalar tavsiye niteliğinde olmayıp genel bilgi olarak kabul edilmelidir. Emin olmadığınız durumlarda sağlık personeline başvurunuz." +
                    "<h4>Hesaplamalar</h4>Uygulamada basit 4 işlem hesaplamaları yapılmaktadır. Uygulamanın kullanımından kullanıcı sorumlu olup kullanım hataları ve yanlış girişlerden " +
                    "uygulamanın yapımcıları sorumlu tutulamaz. Elde edilen sonuçları ve hesaplamaları kontrol etmek kullanıcının yükümlülüğündedir." +
                    "<h4>Besin Değerleri</h4>Uygulamada kullanılan besin değerleri referansları verilen kaynaklardan ve üreticilerin verdiği besin değerlerinden derlenmektedir. " +
                    "Besin değerlerinin doğruluğu ve/veya güncelliği taahhüt edilmemektedir. Besin değerlerinin kontrol edilmesinden kullanıcı sorumludur."
        )

    }
}