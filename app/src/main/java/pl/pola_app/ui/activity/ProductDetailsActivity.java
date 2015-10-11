package pl.pola_app.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import pl.pola_app.R;
import pl.pola_app.model.Product;

public class ProductDetailsActivity extends Activity {

    @Bind(R.id.product_info_card)
    CardView productInfoCard;

    @Bind(R.id.company_name)
    TextView tv_companyName;

    @Bind(R.id.company_plRnD)
    TextView tv_plRnD;

    @Bind(R.id.company_plWorkers)
    TextView tv_plWorkers;

    @Bind(R.id.company_plCapital)
    TextView tv_plCapital;

    @Bind(R.id.company_plTaxes)
    TextView tv_plTaxes;

    @Bind(R.id.company_plBrand)
    TextView tv_plBrand;

    @BindString(R.string.pl_rnd_workers)
    String plRnDWorkers;

    @BindString(R.string.pl_workers)
    String plWorkers;

    @BindString(R.string.pl_capital)
    String plCapital;

    @BindString(R.string.pl_taxes)
    String plTaxes;

    @BindString(R.string.pl_brand)
    String plBrand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        Product product = Parcels.unwrap(extras.getParcelable(Product.class.getName()));

        if(product.verified == false) {
            productInfoCard.setCardBackgroundColor(Color.GRAY);
        }

        tv_companyName.setText(product.company.name);

        if(product.company.plRnD != null) {
            tv_plRnD.setText(String.format(plRnDWorkers, product.company.plRnD));
        }

        if(product.company.plWorkers != null) {
            tv_plWorkers.setText(String.format(plWorkers, product.company.plWorkers));
        }

        if(product.company.plCapital != null) {
            tv_plCapital.setText(String.format(plCapital, product.company.plCapital));
        }

        if(product.company.plTaxes != null) {
            tv_plTaxes.setText(String.format(plTaxes, product.company.plTaxes));
        }

        if(product.company.plBrand != null) {
            tv_plBrand.setText(String.format(plBrand, product.company.plBrand));
        }
    }
}
