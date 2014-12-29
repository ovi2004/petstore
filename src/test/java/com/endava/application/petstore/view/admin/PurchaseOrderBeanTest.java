package com.endava.application.petstore.view.admin;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.endava.application.petstore.exceptions.ValidationException;
import com.endava.application.petstore.model.Address;
import com.endava.application.petstore.model.Category;
import com.endava.application.petstore.model.Country;
import com.endava.application.petstore.model.CreditCard;
import com.endava.application.petstore.model.CreditCardType;
import com.endava.application.petstore.model.Customer;
import com.endava.application.petstore.model.Item;
import com.endava.application.petstore.model.OrderLine;
import com.endava.application.petstore.model.Product;
import com.endava.application.petstore.model.PurchaseOrder;
import com.endava.application.petstore.view.shopping.ShoppingCartItem;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

@RunWith(Arquillian.class)
public class PurchaseOrderBeanTest
{

   // ======================================
   // =             Attributes             =
   // ======================================

   @Inject
   private PurchaseOrderBean purchaseorderbean;

   // ======================================
   // =             Deployment             =
   // ======================================

   @Deployment
   public static JavaArchive createDeployment()
   {
      return ShrinkWrap.create(JavaArchive.class)
            .addClass(PurchaseOrderBean.class)
            .addClass(PurchaseOrder.class)
            .addClass(Country.class)
            .addClass(Address.class)
            .addClass(Customer.class)
            .addClass(CreditCard.class)
            .addClass(CreditCardType.class)
            .addClass(OrderLine.class)
            .addClass(Category.class)
            .addClass(Product.class)
            .addClass(Item.class)
            .addClass(ShoppingCartItem.class)
            .addClass(ValidationException.class)
            .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
   }

   // ======================================
   // =             Test Cases             =
   // ======================================

   @Test
   public void should_be_deployed()
   {
      Assert.assertNotNull(purchaseorderbean);
   }

   @Test @Ignore
   public void should_crud()
   {
      // Creates an object
      Country country = new Country("DV", "Dummy value", "Dummy value", "DMV", "DMV");
      Address address = new Address("78 Gnu Rd", "Texas", "666", country);
      Customer customer = new Customer("Paul", "Mc Cartney", "pmac", "pmac", "paul@beales.com", address);
      CreditCard creditCard = new CreditCard("1234", CreditCardType.MASTER_CARD, "10/12");
      Set<OrderLine> orderLines = new HashSet<OrderLine>();
      PurchaseOrder purchaseOrder = new PurchaseOrder(customer, creditCard, address);
      purchaseOrder.setOrderLines(orderLines);
      purchaseOrder.setDiscount(12.5F);

      // Inserts the object into the database
      purchaseorderbean.setPurchaseOrder(purchaseOrder);
      purchaseorderbean.create();
      purchaseorderbean.update();
      purchaseOrder = purchaseorderbean.getPurchaseOrder();
      assertNotNull(purchaseOrder.getId());

      // Finds the object from the database and checks it's the right one
      purchaseOrder = purchaseorderbean.findById(purchaseOrder.getId());
      assertEquals(new Float(12.5F), purchaseOrder.getDiscountRate());

      // Deletes the object from the database and checks it's not there anymore
      purchaseorderbean.setId(purchaseOrder.getId());
      purchaseorderbean.create();
      purchaseorderbean.delete();
      purchaseOrder = purchaseorderbean.findById(purchaseOrder.getId());
      assertNull(purchaseOrder);
   }

   @Test
   public void should_paginate()
   {
      // Creates an empty example
      PurchaseOrder example = new PurchaseOrder();

      // Paginates through the example
      purchaseorderbean.setExample(example);
      purchaseorderbean.paginate();
      assertTrue((purchaseorderbean.getPageItems().size() == purchaseorderbean.getPageSize()) || (purchaseorderbean.getPageItems().size() == purchaseorderbean.getCount()));
   }
}
