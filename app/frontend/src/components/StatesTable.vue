<template>
  
  <div class="states-table">

    <div class="section-title">
      STATES
    </div>

    <table>
      <thead>
        <tr>
          <th>NAME</th>
          <th>RULER</th>
          <th>MED. STATES</th>
          <th>REGIONS</th>
          <th>POPULATION</th>
          <th>STATE TYPE</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in 6" :key="item">
          <td>Statename</td>
          <td>
            <div>Name</div>
            <small>TITLE</small>
          </td>
          <td>00</td>
          <td>000</td>
          <td>0.000.000</td>
          <td>Statetype</td>
        </tr>
      </tbody>
    </table>
    
  </div>
</template>

<script setup>
</script>