<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


  <xsl:template match="@name">
    <xsl:attribute name="name">
	  <xsl:value-of select="."/>
	</xsl:attribute>
  </xsl:template>

  <xsl:template match="@value">
    <xsl:attribute name="value">
	  <xsl:value-of select="."/>
	</xsl:attribute>
  </xsl:template>

  <xsl:template match="@default">
    <xsl:attribute name="value">
	  <xsl:value-of select="."/>
	</xsl:attribute>
  </xsl:template>

  <xsl:template match="enumeration">
    <option>
	  <xsl:apply-templates select="@label|@value"/>
	</option>
  </xsl:template>

  <xsl:template match="@label">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="@dataType">
    <xsl:variable name="dataType" select="."/>
	<xsl:choose>
	  <xsl:when test='$dataType="xsd:date"'>
	  </xsl:when>
	  <xsl:when test='$dataType="xsd:int"'>
	  </xsl:when>
	  <xsl:when test='$dataType="xsd:string"'>
	  </xsl:when>
	</xsl:choose>
  </xsl:template>

  <xsl:template match="property">
	<div class="formProperty">
	  <span class="formPropertyName"><xsl:apply-templates select="@label"/>:</span>
	  <span>
        <xsl:variable name="enums" select="count(facets/enumeration)"/>
		<xsl:choose>
		  <xsl:when test='$enums &gt; 0'>
		    <select>
			  <xsl:apply-templates select="@name"/>
			  <xsl:apply-templates select="facets/enumeration"/>
			</select>
		  </xsl:when>
		  <xsl:otherwise>
	        <input>
			  <xsl:apply-templates select="@name"/>
			  <xsl:apply-templates select="@default"/>
	          <xsl:apply-templates select="@dataType"/>
	        </input>
		  </xsl:otherwise>
		</xsl:choose>
	  </span>
	  <span class="formPropertyDescription">
	    <xsl:apply-templates select="description"/>
	  </span>
	</div>
  </xsl:template>

  <xsl:template match="description">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="itemType">
    <div>
	  <div><xsl:apply-templates select="description"/></div>
      <form>
	    <xsl:apply-templates select="@name"/>
	    <xsl:apply-templates select="property"/>
		<div>
			<input type="button" value="Execute Query"/>
			<input type="button" value="Subscribe To Query"/>
		</div>
	  </form>
	</div>
  </xsl:template>
	
  <!-- Identity function - Simply pass things through! -->
  <xsl:template match="@*|*|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="*|@*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
