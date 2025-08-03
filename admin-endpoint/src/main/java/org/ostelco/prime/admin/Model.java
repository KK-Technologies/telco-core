// Converted from Kotlin: Model.kt
package org.ostelco.prime.admin

import org.ostelco.prime.model.Plan
import org.ostelco.prime.model.Product

package org.ostelco.prime.admin

import org.ostelco.prime.model.Plan
import org.ostelco.prime.model.Product

public public class CreatePlanRequest {
    private Plan plan;
    private String stripeProductName;
    private Product planProduct;

    public CreatePlanRequest(Plan plan, String stripeProductName, Product planProduct) {
        this.plan = plan;
        this.stripeProductName = stripeProductName;
        this.planProduct = planProduct;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public String getStripeproductname() {
        return stripeProductName;
    }

    public void setStripeproductname(String stripeProductName) {
        this.stripeProductName = stripeProductName;
    }

    public Product getPlanproduct() {
        return planProduct;
    }

    public void setPlanproduct(Product planProduct) {
        this.planProduct = planProduct;
    }

}