package com.example.dami;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestAdapterTest {

    private RequestAdapter adapter;
    private List<BloodDonationRequest> requests;

    @Mock
    private BloodDonationRequest mockRequest;

    @Mock
    private BloodCenter mockBloodCenter;

    @Before
    public void setUp() {
        requests = new ArrayList<>();
        adapter = new RequestAdapter(requests);
    }

    @Test
    public void testBindViewHolderWithNullBloodCenter() {
        // Create a request with null blood center
        BloodDonationRequest request = new BloodDonationRequest();
        request.setBloodType("A+");
        request.setQuantity(2.0);
        request.setStatus("Pending");
        request.setCreatedAt(LocalDateTime.now());
        // Blood center is null by default

        requests.add(request);
        
        // Create a mock view holder
        View view = mock(View.class);
        TextView bloodTypeTextView = mock(TextView.class);
        TextView quantityTextView = mock(TextView.class);
        TextView centerTextView = mock(TextView.class);
        TextView statusTextView = mock(TextView.class);
        TextView dateTextView = mock(TextView.class);

        when(view.findViewById(R.id.bloodTypeTextView)).thenReturn(bloodTypeTextView);
        when(view.findViewById(R.id.quantityTextView)).thenReturn(quantityTextView);
        when(view.findViewById(R.id.centerTextView)).thenReturn(centerTextView);
        when(view.findViewById(R.id.statusTextView)).thenReturn(statusTextView);
        when(view.findViewById(R.id.dateTextView)).thenReturn(dateTextView);

        RequestAdapter.ViewHolder viewHolder = adapter.new ViewHolder(view);
        
        // Test the bind method
        adapter.onBindViewHolder(viewHolder, 0);
        
        // Verify that the center text is set to "Center not specified"
        // Note: In a real test, you would need to verify the actual text set on the TextView
        // This is just a basic structure to show how to test the null case
    }

    @Test
    public void testBindViewHolderWithValidBloodCenter() {
        // Create a request with valid blood center
        BloodDonationRequest request = new BloodDonationRequest();
        request.setBloodType("B+");
        request.setQuantity(1.5);
        request.setStatus("Approved");
        request.setCreatedAt(LocalDateTime.now());
        
        BloodCenter bloodCenter = new BloodCenter();
        bloodCenter.setNamecenter("Test Center");
        request.setBloodCenter(bloodCenter);

        requests.add(request);
        
        // Create a mock view holder
        View view = mock(View.class);
        TextView bloodTypeTextView = mock(TextView.class);
        TextView quantityTextView = mock(TextView.class);
        TextView centerTextView = mock(TextView.class);
        TextView statusTextView = mock(TextView.class);
        TextView dateTextView = mock(TextView.class);

        when(view.findViewById(R.id.bloodTypeTextView)).thenReturn(bloodTypeTextView);
        when(view.findViewById(R.id.quantityTextView)).thenReturn(quantityTextView);
        when(view.findViewById(R.id.centerTextView)).thenReturn(centerTextView);
        when(view.findViewById(R.id.statusTextView)).thenReturn(statusTextView);
        when(view.findViewById(R.id.dateTextView)).thenReturn(dateTextView);

        RequestAdapter.ViewHolder viewHolder = adapter.new ViewHolder(view);
        
        // Test the bind method
        adapter.onBindViewHolder(viewHolder, 0);
        
        // Verify that the center text is set to the blood center name
        // Note: In a real test, you would need to verify the actual text set on the TextView
        // This is just a basic structure to show how to test the valid case
    }
} 