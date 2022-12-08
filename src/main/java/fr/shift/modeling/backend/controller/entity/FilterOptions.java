package fr.shift.modeling.backend.controller.entity;
/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */
public class FilterOptions {

    double scoreThreshold;
    int maxElements;

    boolean excludeDefaultAttributes = true;

    public FilterOptions(double scoreThreshold, int maxElements) {
        this.scoreThreshold = scoreThreshold;
        this.maxElements = maxElements;
    }

    public FilterOptions() {
        this.scoreThreshold = 0d;
        this.maxElements = -1;
    }

    public double getScoreThreshold() {
        return scoreThreshold;
    }

    public int getMaxElements() {
        return maxElements;
    }

    public boolean isExcludeDefaultAttributes() {
        return excludeDefaultAttributes;
    }

    @Override
    public String toString() {
        return "FilterOptions{" +
                "scoreThreshold=" + scoreThreshold +
                ", maxElements=" + maxElements +
                ", excludeDefaultAttributes=" + excludeDefaultAttributes +
                '}';
    }
}
