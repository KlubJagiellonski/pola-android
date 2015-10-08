package pl.pola_app.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.pola_app.R;
import pl.pola_app.model.Product;

public class ProductDetailsActivity extends Activity {

    @Bind(R.id.company_name)
    TextView companyName;

    @Bind(R.id.company_address)
    TextView companyAddress;

    @Bind(R.id.company_nip)
    TextView companyNip;

    @Bind(R.id.company_plCapital)
    TextView companyPlCapital;

    @Bind(R.id.company_plCapital_notes)
    TextView companyPlCapitalNotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        Product product = Parcels.unwrap(extras.getParcelable(Product.class.getName()));

        companyName.setText(product.company.name);
        companyAddress.setText(product.company.address);
        companyNip.setText("NIP: " + product.company.nip);
        companyPlCapital.setText("Udział polskiego kapitału: " + product.company.plCapital + "%");
        companyPlCapitalNotes.setText(product.company.plCapital_notes);

    }
}
